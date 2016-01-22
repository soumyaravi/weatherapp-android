package soumya.weatherforecast.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.hamweather.aeris.communication.AerisCallback;
import com.hamweather.aeris.communication.AerisEngine;
import com.hamweather.aeris.communication.EndpointType;
import com.hamweather.aeris.maps.AerisMapView;
import com.hamweather.aeris.maps.AerisMapView.AerisMapType;
import com.hamweather.aeris.maps.MapViewFragment;
import com.hamweather.aeris.maps.interfaces.OnAerisMapLongClickListener;
import com.hamweather.aeris.model.AerisResponse;
import com.hamweather.aeris.tiles.AerisTile;

public class MapFragment extends MapViewFragment implements
        OnAerisMapLongClickListener, AerisCallback, GoogleMap.OnCameraChangeListener, GoogleMap.OnMarkerClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_interactive_maps, container, false);
        AerisEngine.initWithKeys(this.getString(com.android.weather.R.string.aeris_client_id), this.getString(com.android.weather.R.string.aeris_client_secret), String.valueOf(this));
        mapView = (AerisMapView)view.findViewById(R.id.aerisfragment_map);
        mapView.init(savedInstanceState, AerisMapType.GOOGLE);


        MapActivity activity = (MapActivity) getActivity();
        double lat = activity.getLat();
        double lon = activity.getLong();
       /* GoogleMap googleMap;
        googleMap = this.mapView.getMap();
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.setOnCameraChangeListener(this);
        googleMap.setOnMarkerClickListener(this);*/
        mapView.moveToLocation(new LatLng(lat, lon), 10);
        //CameraUpdate center=CameraUpdateFactory.newLatLng(new LatLng(lat, lon));
        mapView.addLayer(AerisTile.RADAR);
        // mapView.addLayer(AerisTile.RADAR);
        //mapView.addLayer(AerisTile.SAT_VISIBLE);
        // CameraUpdate zoom=CameraUpdateFactory.zoomTo(12);

        //googleMap.moveCamera(center);
        //googleMap.animateCamera(zoom);
        mapView.setOnAerisMapLongClickListener(this);
        return view;
    }

    @Override
    public void onMapLongClick(double lat, double longitude) {
        // code to handle map long press. i.e. Fetch current conditions?
        // see demo app MapFragment.java
    }

    @Override
    public void onResult(EndpointType endpointType, AerisResponse aerisResponse) {

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}