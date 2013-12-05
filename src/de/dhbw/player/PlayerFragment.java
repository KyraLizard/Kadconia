package de.dhbw.player;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.dhbw.navigation.R;

/**
 * Created by Mark on 05.12.13.
 */
public class PlayerFragment extends Fragment
    /*
    Javascript-Code zum Ausgeben der Spielernamen (map.kadcon.de)
    var playerList = Object.keys(DynMap.prototype.players);
    for (var i=0; i<playerList.length; i++)
	    console.log(i+1 + ": " + playerList[i]);
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player, container, false);
    }
}