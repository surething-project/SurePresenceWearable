package pt.ulisboa.tecnico.surething.wearable.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import com.google.type.LatLng;

import java.util.UUID;

import eu.surething_project.core.EpochTime;
import eu.surething_project.core.Location;
import eu.surething_project.core.LocationClaim;
import eu.surething_project.core.Signature;
import eu.surething_project.core.SignedLocationClaim;
import eu.surething_project.core.Time;

public class CoreHandler {
    // Automatic Location Claim builder
    // ProverId comes from shared preferences (user must be logged in)

    private final static String SIGNATURE_ALGORITHM = "SHA256WithRSA";

    public static Signature createSignature(byte[] signature){
        return Signature.newBuilder()
                .setValue(ByteString.copyFrom(signature))
                .setCryptoAlgo(SIGNATURE_ALGORITHM).build();
    }

    public static SignedLocationClaim createSignedLocationClaim(LocationClaim claim, Signature signature){
        return SignedLocationClaim.newBuilder()
                .setClaim(claim)
                .setProverSignature(signature).build();
    }


    public static LocationClaim createLocationClaim(Context context, android.location.Location location){
        String id = "";
        if(SaveSharedPreference.getPrefUserName(context).equals("")){ //no login
            id = "miguel.francisco1998@gmail.com";
        }
        else{
            id = SaveSharedPreference.getPrefUserName(context);
        }
        return LocationClaim.newBuilder()
                                    .setProverId(id)
                                    .setClaimId(UUID.randomUUID().toString())
                                    .setLocation(Location.newBuilder().setLatLng(LatLng.newBuilder().setLatitude(location.getLatitude()).setLongitude(location.getLongitude()).build()).build())
                                    .setTime(Time.newBuilder().setTimestamp(Timestamp.newBuilder().setSeconds(System.currentTimeMillis()/1000).build()).build())
                                    // use the nonce used to communicate with the kiosk as evidence?
                                    .build();
    }

}
