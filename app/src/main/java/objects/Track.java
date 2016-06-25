package objects;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.colinforzeal.blury.R;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import application.App;
import utils.NetworkConnector;
import utils.Utils;

public class Track implements Parcelable{
    private String name;
    private String artist;
    private Bitmap image;
    private Bitmap icon;
    private int color;

    private boolean hasDefaultImage;

    private static final Resources res;

    public Track (){
        /*
            Empty constructor. Compiler can't generate it
            because we have private constructor below
         */
    }

    static {
        //Getting application's resources
        res = App.getContext().getResources();
    }

    /* We need this for CREATOR
     */
    private Track (Parcel in){
        String [] strings = new String[2];
        in.readStringArray(strings);
        this.name = strings[0];
        this.artist = strings[1];

        Bitmap [] bitmaps = in.createTypedArray(Bitmap.CREATOR);
        if (image != null){
            image.recycle();
        }
        image = bitmaps[0];
        if (icon != null){
            icon.recycle();
        }
        icon = bitmaps[1];

        this.color = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{name, artist});
        dest.writeTypedArray((new Parcelable[]{image, icon}), flags);
        dest.writeInt(color);
    }

    public static final Parcelable.Creator<Track> CREATOR = new Parcelable.Creator<Track>(){

        @Override
        public Track createFromParcel(Parcel source) {
            return new Track(source);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

    public void setName(String name) {
        this.name = name;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setImage(String imageURL) throws IOException{
        if (imageURL == null){
            return;
        }

        try{
            BufferedInputStream buf =
                    new BufferedInputStream(NetworkConnector.getInputStreamFromHttp(imageURL));

            image = BitmapFactory.decodeStream(buf);
            icon = Utils.cropBitmapToCircle(image);
            setColor(false);

            hasDefaultImage = false;

            buf.close();
        }
        catch (FileNotFoundException e){
            Log.e(e.getClass().getName(), "Can't find cover.");
        }
        catch (IOException e){
            Log.e(e.getClass().getName(), "Can't work with cover stream.");
            throw new IOException();
        }
    }

    public void setDefaultImage(){
        image = BitmapFactory.decodeResource(res, R.drawable.default_cover);
        icon = Utils.cropBitmapToCircle(image);
        setColor(true);

        /*
           It is impossible to transmit drawable
           between two activities
         */
        hasDefaultImage = true;
    }

    public void setColor(boolean defaultCover){
        if (defaultCover){
            color = res.getColor(R.color.colorPrimary);
        }
        else{
            color = Utils.getFabColor(image);

            if (color == 0){
                color = res.getColor(R.color.colorPrimary);
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public Bitmap getImage() {return image;}

    public Bitmap getIcon(){return icon;}

    public int getColor(){return color;}

    public boolean hasDefaultImage(){return hasDefaultImage;}
}
