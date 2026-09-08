package com.bot;

import com.bot.dashboard.Frame;
import com.bot.massive.MassiveClient;
import com.bot.util.*;

import javax.swing.*;
import java.io.IOException;
import java.util.HashMap;

public class Main {

    public static HashMap<String, String> env;
    public static MassiveClient massive;

    @SuppressWarnings("unused")
    public static void main(String[] args) throws IOException {
        env = DotEnv.get(".env");
        massive = new MassiveClient(env.get("api_key"));

        SwingUtilities.invokeLater(Frame::new);
    }

}