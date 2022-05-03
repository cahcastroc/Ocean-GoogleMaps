package me.camila.oceangooglemaps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Color.parseColor
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import me.camila.oceangooglemaps.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //---------Adiciona uma marca no Mapa
        val localInfnet = LatLng(-22.906065396808252, -43.17682807647066)

        mMap.addMarker(MarkerOptions().position(localInfnet).title("Infnet - Faculdade da Camila"))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(localInfnet, 18.5f))

        mMap.addCircle(
            CircleOptions()
                .center(localInfnet)
                .radius(50.0)
                .strokeWidth(0f)
                .fillColor(Color.parseColor("#537cdbe7"))
        )

        iniciarLocalizacao() //Inícia a última localização do usuário
    }

    private fun iniciarLocalizacao() {

        //------------Definição LocationManager
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        //-----------Definição Provider

          val locationProvider = LocationManager.NETWORK_PROVIDER

        //--------com GPS_PROVIDER (val locationProvider = LocationManager.GPS_PROVIDER)



        //Permissões
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
           ActivityCompat.requestPermissions(
               this,
               arrayOf(
                   Manifest.permission.ACCESS_FINE_LOCATION,
                   Manifest.permission.ACCESS_COARSE_LOCATION
               ),
               1
           )
            return
        }

        val ultimaLocalizacao = locationManager.getLastKnownLocation(locationProvider)


        //Check se existe a última localização

        ultimaLocalizacao?.let {
            val latLng = LatLng(it.latitude, it.longitude)

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.5f))

        }

        //Localização em tempo real

        locationManager.requestLocationUpdates(
            locationProvider, //Qual provider
             100,   //Intervalo de tempo da atualização
             1F   //Distância
        )

        {
            val latLng = LatLng(it.latitude, it.longitude)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16.5f))
        }

    }

    override fun onRequestPermissionsResult( //Resultado das permissões
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && (grantResults[0] == PackageManager.PERMISSION_GRANTED
            || grantResults[1]== PackageManager.PERMISSION_GRANTED)){
            iniciarLocalizacao()
        }
    }
}
