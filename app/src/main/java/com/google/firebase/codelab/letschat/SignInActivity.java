
package com.google.firebase.codelab.letschat;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST = 0;
    private static final int RESULT_LOAD_IMAGE = 1;

    private SharedPreferences sp;

    private Button mSignInButton;
    private ImageView imgEditProfilePic;
    private CircleImageView imgProfilePic;
    private EditText txtMobile, txtUsername;
    private TextView messagesLogin;

    // Firebase instance variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //recupero lo SharedPreferences
        sp = this.getSharedPreferences("com.google.firebase.codelab.letschat", Context.MODE_PRIVATE);

        // Collega gli oggetti grafici al codice
        mSignInButton = (Button) findViewById(R.id.sign_in_button);
        imgProfilePic = (CircleImageView) findViewById(R.id.profileImage);
        imgEditProfilePic =  (ImageView) findViewById(R.id.imgEditProfilePic);
        txtMobile = (EditText) findViewById(R.id.mobile);
        txtUsername = (EditText) findViewById(R.id.username);
        messagesLogin = (TextView) findViewById(R.id.warningLogin);


        // Set click listeners
        mSignInButton.setOnClickListener(this);
        imgEditProfilePic.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                //cosa fare al click del bottone di login
                checkLogin(txtMobile.getText().toString(), txtUsername.getText().toString());
                break;
            case R.id.imgEditProfilePic:
                //edit profile picture
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
                    }
                }else{
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
                }

                break;

            case R.id.profileImage:
                //show profile picture

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {
            case PERMISSION_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
                } else {
                    Toast.makeText(this, "Permesso negato", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_LOAD_IMAGE:
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    Bitmap bitmap = null;
                    Uri selectedImage = null;
                    try {
                       selectedImage = data.getData();
                       bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        ExifInterface exif = new ExifInterface(getPath(this, selectedImage));
                        String orientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                        switch(orientation){
                            case "6":
                                imgProfilePic.setImageResource(0);
                                imgProfilePic.setBackgroundResource(0);
                                imgProfilePic.setImageBitmap(bitmap);
                                imgProfilePic.setRotation(90);
                                break;
                            case "8":
                                imgProfilePic.setImageResource(0);
                                imgProfilePic.setBackgroundResource(0);
                                imgProfilePic.setImageBitmap(bitmap);
                                imgProfilePic.setRotation(270);
                                break;
                            default:
                                imgProfilePic.setRotation(0);
                                imgProfilePic.setImageResource(0);
                                imgProfilePic.setBackgroundResource(0);
                                imgProfilePic.setImageBitmap(bitmap);
                                break;
                        }
                        sp.edit().putString("profilePic", selectedImage.toString()).apply();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public void checkLogin(String mobile, String user){
        String regex = "^\\d{10}$"; //regex per il numero di cellulare: controlla che abbia 10 cifre
        if(mobile.matches(regex)){
            if(!user.isEmpty()){
                //valid mobile phone and username
                Intent intent = new Intent(SignInActivity.this, VerifyMobileActivity.class);
                intent.putExtra("mobileNumber", mobile);
                intent.putExtra("username", user);
                startActivity(intent);
            }else{
                //username empty
                messagesLogin.setText(R.string.invalid_username);
                messagesLogin.setVisibility(View.VISIBLE);
            }
        }else if(user.isEmpty()){
            //invalid both mobile number and username
            messagesLogin.setText(R.string.login_empty_fields);
            messagesLogin.setVisibility(View.VISIBLE);
        }else{
            //invalid mobile number
            messagesLogin.setText(R.string.invalid_mobile);
            messagesLogin.setVisibility(View.VISIBLE);
        }
    }
}