package me.leok.smartFoodBoxes.fragments;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.UUID;

import me.leok.smartFoodBoxes.R;
import me.leok.smartFoodBoxes.ScaleActivity;
import me.leok.smartFoodBoxes.ScaleBluetoothSerial;
import me.leok.smartFoodBoxes.ScaleData;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeightFragment extends Fragment {

    private static final String TAG = "WeightFragment";

    ScaleActivity mThisActivity;

    TextView weightText;
    ProgressBar weightProgressBar;

    Button tare;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weight, container, false);

        mThisActivity = (ScaleActivity) getActivity();

        weightProgressBar = (ProgressBar) view.findViewById(R.id.weightProgressBar);
        weightText = (TextView) view.findViewById(R.id.weightText);
        tare = (Button) view.findViewById(R.id.tareButton);

        weightProgressBar.setScaleY(8f);

        tare.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mThisActivity.scale.tare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        /*
         * Starts listening for serial data from BT!
         */
        mThisActivity.scale.listen();
        mThisActivity.scale.setmOnDataReceivedListener(new ScaleBluetoothSerial.OnDataReceivedListener() {
            @Override
            public void onDataReceived(final String data) {
                final Gson gson = new Gson();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ScaleData scaleData = new ScaleData(0, "");

                        try {
                            scaleData = gson.fromJson(data, ScaleData.class);
                        } catch (Exception e) {
                            Log.w(TAG, "Error while deserializing JSON: " + e.toString());
                        }

                        weightText.setText(String.format("%sg", String.format("%.1f", scaleData.weight)));
                        weightProgressBar.setProgress((int) scaleData.weight);
                    }
                });
            }
        });

        return view;
    }
}
