package com.example.garbagefinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class mapPage extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    UserDao userDAO;
    User user;
    TextView navHeaderUsername;
    ImageView navHeaderPicture;
    NavigationView navigationView;

    MapView map;
    FusedLocationProviderClient fusedLocationClient;
    MyLocationNewOverlay mLocationOverlay;
    MapTouchOverlay touchOverlay;
    Button toggleSelectButton;
    boolean selectionEnabled = false;
    LinearLayout palletLayout;

    ImageButton greenButton;
    ImageButton orangeButton;
    ImageButton blueButton;
    ImageButton brownButton;
    ImageButton grayButton;

    private void loadUserData(User user) {
        String uriString = user.uri;
        String username = user.name;

        navHeaderUsername.setText(username);

        if (uriString != null && !uriString.isEmpty()) {
            Uri uri = Uri.parse(uriString);
            Glide.with(this)
                    .load(uri)
                    .centerCrop()
                    .into(navHeaderPicture);
        }

        // Load saved locations and add them as markers
        if (user.locations != null && !user.locations.isEmpty()) {
            for (String locationString : user.locations) {
                String[] parts = locationString.split(",", 4); // Updated delimiter
                if (parts.length == 4) {
                    try {
                        double latitude = Double.parseDouble(parts[0]);
                        double longitude = Double.parseDouble(parts[1]);
                        String markerName = parts[2];
                        String description = parts[3];
                        GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                        addMarker(geoPoint, markerName, description);
                    } catch (NumberFormatException e) {
                        Log.e("loadUserData", "Invalid location format: " + locationString, e);
                    }
                } else {
                    Log.e("loadUserData", "Invalid location format: " + locationString);
                }
            }
        } else {
            Log.d("loadUserData", "No locations to load");
        }
    }

    private void initializeMap() {
        // Initialize the MapView
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        // Set default zoom level and center point
        map.getController().setZoom(18.0);  // Set a higher zoom level for a closer view
        map.getController().setCenter(new GeoPoint(0.0, 0.0));  // Set to an arbitrary location for initialization

        // Enable location overlay
        mLocationOverlay = new MyLocationNewOverlay(map);
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(mLocationOverlay);

        // Initialize touch overlay but do not add it yet
        touchOverlay = new MapTouchOverlay(this, map, user, userDAO);
        touchOverlay.setEnabled(false); // Start with touch overlay disabled
    }

    private void getUserLocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
            return;
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations, this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            GeoPoint userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                            map.getController().setCenter(userLocation);
                            map.getController().setZoom(18.0);  // Ensure zoom level is set after getting location

                            // Add a marker at the user's location
                            addMarker(userLocation, "Your Location", "This is your current location.");
                        }
                    }
                });
    }

    private void addMarker(GeoPoint point, String title, String description) {
        Marker marker = new Marker(map);
        marker.setAnchor(0.3f, 0.3f);

        Drawable icon = ContextCompat.getDrawable(this, getMarkerIcon(title));
        marker.setIcon(icon);
        marker.setPosition(point);
        marker.setTitle(title);
        marker.setSnippet(description);
        map.getOverlays().add(marker);
        map.invalidate(); // Refresh the map to show the new marker
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();
            } else {
                // Permission denied, show message to user
                Log.e("mapPage", "Permission denied for location");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // This will refresh the osmdroid configuration onResume
        Configuration.getInstance().load(getApplicationContext(), getPreferences(MODE_PRIVATE));
        if (map != null) {
            map.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (map != null) {
            map.onPause();
        }
    }

    int getMarkerIcon(String title) {
        switch (title) {
            case "Green Bin":
                return R.drawable.marker_green;
            case "Orange Bin":
                return R.drawable.marker_orange;
            case "Blue Bin":
                return R.drawable.marker_blue;
            case "Brown Bin":
                return R.drawable.marker_brown;
            default:
                return R.drawable.marker_gray;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_page);

        Configuration.getInstance().load(getApplicationContext(), getPreferences(MODE_PRIVATE));

        initializeMap();  // Ensure the map is initialized

        // Check for location permissions
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getUserLocation();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
        }

        DatabaseClient db = DatabaseClient.getInstance(getApplicationContext());
        userDAO = db.getDb().userDao();

        Intent intent = getIntent();
        String username = intent.getStringExtra("name");

        // Set up the navigation view and header view
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navHeaderUsername = headerView.findViewById(R.id.name);
        navHeaderPicture = headerView.findViewById(R.id.picture);

        palletLayout = findViewById(R.id.palleteLayout);

        palletLayout.setEnabled(false);
        palletLayout.setVisibility(View.INVISIBLE);

        greenButton = findViewById(R.id.green);
        orangeButton = findViewById(R.id.orange);
        blueButton = findViewById(R.id.blue);
        brownButton = findViewById(R.id.brown);
        grayButton = findViewById(R.id.gray);

        // Make sure username is not null
        if (username != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    user = userDAO.findUserByUsername(username);
                    if (user == null) {
                        Log.e("mapPage", "User not found in database");
                    } else {
                        Log.d("mapPage", "User found: " + user.name);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadUserData(user);
                                // Ensure user is passed to touchOverlay
                                touchOverlay = new MapTouchOverlay(mapPage.this, map, user, userDAO);
                            }
                        });
                    }
                }
            }).start();
        } else {
            Log.e("mapPage", "Username is null in intent");
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_logout) {
                    Intent intent = new Intent(mapPage.this, Login.class);
                    startActivity(intent);
                }
                else if(item.getItemId() == R.id.nav_about){
                    Intent intent = new Intent(mapPage.this, aboutMe.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
                else if (item.getItemId() == R.id.nav_settings) {
                    Intent intent = new Intent(mapPage.this, settingsPage.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
                return false;
            }
        });

        // Setup toggle button for enabling/disabling marker selection
        toggleSelectButton = findViewById(R.id.toggleSelectButton);
        toggleSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionEnabled = !selectionEnabled;
                touchOverlay.setEnabled(selectionEnabled);
                if (selectionEnabled) {
                    palletLayout.setEnabled(true);
                    palletLayout.setVisibility(View.VISIBLE);
                    map.getOverlays().add(touchOverlay);  // Enable selection by adding the overlay
                    toggleSelectButton.setText("Disable Selection");
                    Toast.makeText(mapPage.this, "Selection Enabled", Toast.LENGTH_SHORT).show();
                } else {
                    palletLayout.setEnabled(false);
                    palletLayout.setVisibility(View.INVISIBLE);

                    map.getOverlays().remove(touchOverlay);  // Disable selection by removing the overlay
                    toggleSelectButton.setText("Enable Selection");
                    Toast.makeText(mapPage.this, "Selection Disabled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        greenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                touchOverlay.selectedColor = "green";
            }
        });
        orangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                touchOverlay.selectedColor = "orange";
            }
        });
        blueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                touchOverlay.selectedColor = "blue";
            }
        });
        brownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                touchOverlay.selectedColor = "brown";
            }
        });
        grayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                touchOverlay.selectedColor = "gray";
            }
        });

    }

}
