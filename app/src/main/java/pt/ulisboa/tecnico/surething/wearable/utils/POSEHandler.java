package pt.ulisboa.tecnico.surething.wearable.utils;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.common.util.Hex;
import com.google.protobuf.ByteString;

import java.security.SecureRandom;
import java.util.HashMap;

import pt.ulisboa.tecnico.surething.pose.Enc_Structure;
import pt.ulisboa.tecnico.surething.pose.HeaderMap;
import pt.ulisboa.tecnico.surething.pose.PoseEncrypt0;
import pt.ulisboa.tecnico.surething.pose.Value;

public class POSEHandler {

    private static final int ALG = 1;    // Label for alg, check RFC 8152
    private static final int VALUE = 10; // Value for AES_GCM
    private static final int IV = 5; // Label for the partial IV
    private static final String CONTEXT = "Encrypt0"; // Encrypt0 object

    public static PoseEncrypt0 populateEncrypt0(Context context, byte[] plaintext, byte[] associated_data, byte[] nonce, PoseEncrypt0 skeleton){
        // the Encrypt0 skeleton only has the protected field
        // we use that same skeleton to create other similar protobuf so that we can populate the remaining fields

        return PoseEncrypt0.newBuilder(skeleton)
                .setUnprotected(createMap(new HashMap<Integer, Value>()))
                .setCiphertext(ByteString.copyFrom(crypto.aeadEncrypt(context, associated_data, plaintext, nonce)))
                .build();

    }

    // Creates a proto map from an hashmap
    public static HeaderMap createMap(HashMap<Integer, Value> map){
        Log.d("MAP", map.toString());
        return HeaderMap.newBuilder().putAllMap(map).build();
    }

    public static Enc_Structure createEnc_Structure(Context context, byte[] plaintext){

        long begin = System.currentTimeMillis();

        HashMap<Integer, Value> map_encrypt0 = new HashMap<>(); // protected field of Encrypt0 structure
        //Lets add the encryption algorithm
        map_encrypt0.put(ALG, Value.newBuilder().setInt(VALUE).build());  // 1:10

        HashMap<Integer, Value> map_enc = new HashMap<>(); // protected field of Enc_Structure
        //Lets add the nonce, which is also the IV
        byte[] nonce = generateNonce();
        map_enc.put(IV, Value.newBuilder().setBstr(ByteString.copyFrom(nonce)).build()); // 5: iv
        PoseEncrypt0 skeleton = PoseEncrypt0.newBuilder().setProtected(createMap(map_encrypt0).toByteString()).build();
        Enc_Structure enc_structure_to_be_serialized = Enc_Structure.newBuilder()
                                        .setContext(CONTEXT)
                                        .setProtected(createMap(map_enc).toByteString())
                                        .setBody(skeleton)
                                        .build();
        Log.d("ALG_MAP_SIZE: ", String.valueOf(skeleton.getProtected().toByteArray().length));
        Log.d("IV_MAP_SIZE: ", String.valueOf(enc_structure_to_be_serialized.getProtected().toByteArray().length));
        Log.d("CONTEXT_SIZE: ", String.valueOf(enc_structure_to_be_serialized.getContext().getBytes().length));
        Log.d("CONTEXT_SIZE: ", String.valueOf(CONTEXT.getBytes().length));
        //Log.d("UNPROTECTED_SIZE: ", String.valueOf(enc_structure_to_be_serialized.().getBytes().length));
        // Enc_Structure with the appropriate fields
        byte[] associated_data = enc_structure_to_be_serialized.toByteArray(); // associated data with protected fields including the nonce
        Enc_Structure enc_structure = Enc_Structure.newBuilder(enc_structure_to_be_serialized)
                                        .setBody(populateEncrypt0(context, plaintext, associated_data, nonce, skeleton))
                                        .build();
        Log.d("pose", enc_structure.toString());
        long end = System.currentTimeMillis();

        Log.d("SIZE OF ENC: ", String.valueOf(enc_structure.toByteArray().length));
        Log.d("SIZE OF CIPHER ", String.valueOf(enc_structure.getBody().getCiphertext().toByteArray().length));

        Log.d("UNPROTECTED_SIZE: ", String.valueOf(enc_structure.getBody().getUnprotected().toByteArray().length));


        return enc_structure;
    }

    // Generates an 12-bytes nonce
    public static byte[] generateNonce(){
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[12];
        secureRandom.nextBytes(bytes);
        return bytes;
    }
}
