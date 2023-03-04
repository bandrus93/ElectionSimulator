package com.innotech.electionsim.view;

import com.innotech.electionsim.model.ElectionSettings;
import com.innotech.electionsim.model.*;

import java.util.*;

public class DisplayManager {

    public static void refresh(String reprint) {
        System.out.println("\033[H\033[2J");
        System.out.flush();
        System.out.println(reprint);
    }

}
