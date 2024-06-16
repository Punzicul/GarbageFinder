package com.example.garbagefinder;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import androidx.core.content.ContextCompat;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import java.util.Objects;

public class MapTouchOverlay extends Overlay {

    private Context context;
    private MapView mapView;
    private boolean enabled = false; // Flag to enable/disable touch overlay
    User user;
    UserDao userDAO;
    String selectedColor = "green";

    public void setColor(String color) {
        this.selectedColor = color;
    }

    public MapTouchOverlay(Context context, MapView mapView, User user, UserDao userDAO) {
        super(context);
        this.context = context;
        this.mapView = mapView;
        this.user = user;
        this.userDAO = userDAO;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
        if (!enabled) {
            return false; // Ignore touches if overlay is disabled
        }

        try {
            // Convert the screen point to a GeoPoint
            Point screenPoint = new Point((int) e.getX(), (int) e.getY());
            GeoPoint geoPoint = (GeoPoint) mapView.getProjection().fromPixels(screenPoint.x, screenPoint.y);

            String markerName;
            String description;

            if (Objects.equals(selectedColor, "green")) {
                markerName = "Green Bin";
                description = "A bin used to store items that can't be sorted (default bin)";
            } else if (Objects.equals(selectedColor, "orange")) {
                markerName = "Orange Bin";
                description = "A bin used to store various packages";
            } else if (Objects.equals(selectedColor, "blue")) {
                markerName = "Blue Bin";
                description = "A bin used to store paper and cardboard";
            } else if (Objects.equals(selectedColor, "brown")) {
                markerName = "Brown Bin";
                description = "A bin used to store organic matter";
            } else {
                markerName = "Gray Bin";
                description = "A bin used to store various metals";
            }

            // Convert GeoPoint to string format "latitude longitude markerName description"
            String locationString = geoPoint.getLatitude() + "," + geoPoint.getLongitude() + "," + markerName + "," + description;

            user.addLocation(locationString);

            // Perform database update in the background
            new UpdateUserTask(userDAO, user).execute();

            // Add a marker at the GeoPoint
            addMarker(geoPoint, markerName, description);

        } catch (Exception ex) {
            Log.e("MapTouchOverlay", "Error in onSingleTapConfirmed", ex);
        }

        return true;
    }

    private void addMarker(GeoPoint point, String title, String description) {
        try {
            Marker marker = new Marker(mapView);

            // Set the custom icon for the marker
            Drawable icon = ContextCompat.getDrawable(context, getMarkerIcon());
            marker.setIcon(icon);

            // Set the anchor to the bottom center
            marker.setAnchor(0.3f, 0.3f);

            marker.setPosition(point);
            marker.setTitle(title);
            marker.setSnippet(description);
            mapView.getOverlays().add(marker);
            mapView.invalidate(); // Refresh the map to show the new marker
        } catch (Exception ex) {
            Log.e("MapTouchOverlay", "Error in addMarker", ex);
        }
    }

    private int getMarkerIcon() {
        switch (selectedColor) {
            case "green":
                return R.drawable.marker_green;
            case "orange":
                return R.drawable.marker_orange;
            case "blue":
                return R.drawable.marker_blue;
            case "brown":
                return R.drawable.marker_brown;
            default:
                return R.drawable.marker_gray;
        }
    }

    private static class UpdateUserTask extends AsyncTask<Void, Void, Void> {
        private UserDao userDAO;
        private User user;

        UpdateUserTask(UserDao userDAO, User user) {
            this.userDAO = userDAO;
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                userDAO.update(user);
            } catch (Exception ex) {
                Log.e("UpdateUserTask", "Error updating user", ex);
            }
            return null;
        }
    }
}
