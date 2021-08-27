package com.example.mutinydemo.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import io.smallrye.mutiny.Multi;
import io.vertx.core.buffer.Buffer;

@Path("/client/service")
public class ServiceController {

    @GET
    @Path("/{parameter}")
    public String doSomething(@PathParam("parameter") String parameter) {
        return String.format("Processed parameter value '%s'", parameter);
    }

    @GET
    @Path("/pi")
    public Multi<Buffer> getPiDecimal() {
        return Multi.createFrom().empty();
    }

    public void getAllPiDecimal() {
        URL piFileURL = getClass().getResource("pi.txt");
        File piFile = new File(piFileURL.getPath());
        try (FileInputStream fr = new FileInputStream(piFile)) {
            FileChannel channel = fr.getChannel();

            ByteBuffer buf = ByteBuffer.allocate(10);

            channel.read(buf);
            buf.flip();

            String string = new String(buf.array(), StandardCharsets.UTF_8);

            System.out.println(string);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
