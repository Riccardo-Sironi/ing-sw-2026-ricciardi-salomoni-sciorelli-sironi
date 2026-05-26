package it.polimi.gc06.mesos.network;

/**
 * A little helper class holding everything you need to know about the current connection string.
 *
 * @param tech     The protocol flavor ("RMI" or "SOCKET") you went with.
 * @param ip       Where on the web the server lives.
 * @param port     The gateway entrance you knocked on.
 * @param nickname The player alias attached to these details.
 */
public record ConnectionDetails(String tech, String ip, int port, String nickname) {
}