package wang.ismy.wolapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Title: Api
 * @description:
 * @author: cjiping@linewell.com
 * @since: 2021年12月17日 15:06
 */
@RestController
public class Api {

    /**
     * wake it up
     * @param ip
     * @param mac
     * @param port
     * @return
     * @throws IOException
     */
    @RequestMapping("wake")
    public String wake(@RequestParam String ip,
                       @RequestParam String mac,
                       @RequestParam(value = "port", defaultValue = "7") int port
                       ) throws IOException {
        wakeOnLan(InetAddress.getByName(ip), mac, port);
        return "success";
    }

    /**
     * detect the machine are online
     * @param ip
     * @return
     */
    @RequestMapping("status")
    public String status(@RequestParam String ip, @RequestParam(value = "timeout", defaultValue = "3000") int timeout){
        try {
            boolean reachable = InetAddress.getByName(ip).isReachable(timeout);
            return reachable ? "online": "offline";
        } catch (Exception e) {
            e.printStackTrace();
            return "offline";
        }
    }

    private byte[] getBytesFromString(String macAddr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macAddr.split("\\:|\\-");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Mac Address must contain 6 bytes separeted by colon or dash");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        } catch (NumberFormatException nex) {
            throw new IllegalArgumentException("Cannot parse hexademical number. Illegal character.");
        }
        return bytes;
    }

    public byte[] getMagic(String macAddr) throws IllegalArgumentException {
        byte[] bts = getBytesFromString(macAddr);
        return getMagic(bts);
    }

    public byte[] getMagic(byte[] bts) throws IllegalArgumentException {
        byte[] wakeUp = new byte[102];
        for (int i = 0; i < 6; i++) {
            wakeUp[i] = (byte) 0xff;
        }
        for (int i = 6; i < wakeUp.length; i += bts.length) {
            System.arraycopy(bts, 0, wakeUp, i, bts.length);
        }
        return wakeUp;
    }

    public void wakeOnLan(InetAddress ip, byte[] macBytes, int port) throws IOException {

        byte[] magic = getMagic(macBytes);

        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            DatagramPacket dPack = new DatagramPacket(magic, magic.length, ip, port);
            socket.send(dPack);
        } catch (IOException e) {
            throw new IOException("Failed to sent datagram packet.", e);
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    public void wakeOnLan(InetAddress ip, String macAddr, int port) throws IllegalArgumentException, IOException {
        wakeOnLan(ip, getBytesFromString(macAddr), port);
    }
}
