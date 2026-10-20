package com.figuras;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/** Muestra en la terminal el host donde quedó corriendo la app. */
@Component
public class MostrarHost {

    private final Environment env;

    public MostrarHost(Environment env) {
        this.env = env;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void mostrar() {
        String puerto = env.getProperty("local.server.port",
                env.getProperty("server.port", "8080"));
        String ruta = env.getProperty("server.servlet.context-path", "");

        StringBuilder sb = new StringBuilder();
        sb.append("\n  ------------------------------------------------\n");
        sb.append("   App figuras-spring lista\n\n");
        sb.append("   Local:  http://localhost:").append(puerto).append(ruta).append("/\n");

        String ip = ipDeRed();
        if (ip != null) {
            sb.append("   Red:    http://").append(ip).append(':')
              .append(puerto).append(ruta).append("/\n");
        }

        sb.append("   API:    http://localhost:").append(puerto).append(ruta)
          .append("/api/figuras\n\n");
        sb.append("   Presiona Ctrl+C para detener\n");
        sb.append("  ------------------------------------------------\n");

        System.out.println(sb);
    }

    private static String ipDeRed() {
        try {
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) {
                    continue;
                }
                for (InetAddress dir : Collections.list(ni.getInetAddresses())) {
                    if (dir instanceof Inet4Address && dir.isSiteLocalAddress()) {
                        return dir.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            // Sin red: solo se muestra localhost.
        }
        return null;
    }
}
