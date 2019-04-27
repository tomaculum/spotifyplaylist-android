package com.example.spotifyplaylists;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    // Instructions:
    // 1. open https://developer.spotify.com/dashboard/
    // 2. select your application or create a new one
    // 3. copy the client id and replace the one below
    // 4. click on "edit settings" and add the following redirect URI:
    //      spotifyplaylists://callback
    // 5. add the "Android Packages" information in the "edit settings" menu (same as 4.)
    // 5.1 add the android package name: com.example.spotifyplaylists
    // 5.2 open the terminal tab in android studio and pase+run the following command :
    //      keytool -list -v -alias androiddebugkey -keystore ~/.android/debug.keystore
    // (default password is android)
    // 6. press on "save" in the "edit settings" popup and try if it works


    // change this and add your client ID here
    public static final String CLIENT_ID = "2728f152d53b42f1a29e3a967f34a02f";

    // this is how your redirect URI should look like in the spotify dev dashboard
    public static final String REDIRECT_URI = "spotifyplaylists://callback";
    public static final int AUTH_TOKEN_REQUEST_CODE = 2019;

    private String mAccessToken;

    private RecyclerView recyclerView;
    private PlaylistAdapter adapter;

    private int currentOffset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new PlaylistAdapter(new ArrayList<PlaylistSimple>(), getApplicationContext());
        recyclerView.setAdapter(adapter);
    }

    public void onGetUserProfileClicked(View view) {
        if (mAccessToken == null) {
            Log.e("MainActivity", "Error: mAccessToken is null - please sign in first");
            return;
        }

        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(mAccessToken);
        SpotifyService spotify = api.getService();

        Map<String, Object> options = new HashMap<>();
        options.put(SpotifyService.OFFSET, currentOffset);
        options.put(SpotifyService.LIMIT, 50);

        spotify.getMyPlaylists(options, new SpotifyCallback<Pager<PlaylistSimple>>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                // handle error
                Log.e("MainActivity", "Error: getMyPlaylists ...");
            }

            @Override
            public void success(Pager<PlaylistSimple> playlistSimplePager, Response response) {
                // do something
                // for example
                adapter.addItems(playlistSimplePager.items);
            }
        });

        // next time the button gets pressed, load 50 more playlists
        currentOffset += 50;
    }

    public void onRequestTokenClicked(View view) {
        final AuthenticationRequest request = getAuthenticationRequest(AuthenticationResponse.Type.TOKEN);
        AuthenticationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    private AuthenticationRequest getAuthenticationRequest(AuthenticationResponse.Type type) {
        return new AuthenticationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[]{"playlist-read-private"})
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);

        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();
            updateTokenView();
        }
    }

    private void updateTokenView() {
        final TextView tokenView = findViewById(R.id.token_textView);
        tokenView.setText(String.format("AccessToken: %s", mAccessToken));
    }

    private Uri getRedirectUri() {
        return new Uri.Builder()
                .scheme(getString(R.string.com_spotify_sdk_redirect_scheme))
                .authority(getString(R.string.com_spotify_sdk_redirect_host))
                .build();
    }

}
