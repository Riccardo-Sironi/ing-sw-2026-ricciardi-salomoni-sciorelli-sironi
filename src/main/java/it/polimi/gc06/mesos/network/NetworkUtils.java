package it.polimi.gc06.mesos.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

public class NetworkUtils {

    public static String getPublicIpAddress() throws Exception {
        URL url = new URI("https://api.ipify.org").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        connection.setConnectTimeout(3000);
        connection.setReadTimeout(3000);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String ip = in.readLine();
            if (ip != null && !ip.trim().isEmpty()) {
                return ip.trim();
            }
        }
        return "";
    }

    public static String getLocalIpAddress() {
        List<String> validIps = new ArrayList<>();
        List<String> displayNames = new ArrayList<>();

        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                // Ignore offline interfaces, loopback (127.0.0.1) or virtual interfaces ( disabled VPNs )
                if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    byte[] ipBytes = addr.getAddress();

                    // We only work with IPv4 addresses,
                    // and we don't want link-local IPs ( automatically assigned when no valid IP could be obtained )
                    if (ipBytes.length == 4 && !addr.isLinkLocalAddress()) {
                        validIps.add(addr.getHostAddress());
                        displayNames.add(addr.getHostAddress() + " - " + networkInterface.getDisplayName());
                    }
                }
            }
        } catch (SocketException e) {
            System.err.println("Error reading the local network interfaces");
        }

        // No network found
        if (validIps.isEmpty()) {
            System.out.println("No network found. Falling back to localhost");
            return "127.0.0.1";
        }

        // Found ONLY one working interface. Automatically assigning the IP.
        if (validIps.size() == 1) {
            System.out.println("Found one network interface: " + displayNames.getFirst());
            return validIps.getFirst();
        }

        // Found multiple network interfaces
        System.out.println("\nFound multiple network interfaces. Please select the one you wish to use:");
        for (int i = 0; i < displayNames.size(); i++) {
            System.out.println(i + ") " + displayNames.get(i));
        }

        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        while (choice < 0 || choice > validIps.size() - 1) {
            System.out.print("Please select a valid interface (0-" + (validIps.size() - 1) + "): ");
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                // Invalid input, loop will prompt again
            }
        }

        return validIps.get(choice);
    }
}
