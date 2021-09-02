package com.example.mutinydemo.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
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
    @Produces({ "application/octet-stream" })
    public Multi<Buffer> getPiDecimal() throws FileNotFoundException, IOException {
        Multi<Long> delay = Multi.createFrom().ticks().every(Duration.ofMillis(100)).onOverflow().drop();

        return Multi.createBy().combining().streams(delay, getAllPiDecimal()).using((x, item) -> item).onItem()
                .invoke(a -> System.out.println(new String(a, StandardCharsets.UTF_8))).onItem()
                .transform(bytes -> Buffer.buffer(bytes));
    }

    @ServerExceptionMapper
    public Uni<Response> mapException(Exception ex) {
        return Uni.createFrom().item(Response.status(404).build());
    }

    private static final int CHUNK_SIZE = 10;

    public static Multi<byte[]> getAllPiDecimal() throws FileNotFoundException, IOException {
        URL piFileURL = new ServiceController().getClass().getResource("pi.txt");
        File piFile = new File(piFileURL.getPath());

        long size = 0l;

        try (FileChannel channel = new FileInputStream(piFile).getChannel()) {
            size = channel.size();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long numberOfChunks = size / CHUNK_SIZE;

        Stream<byte[]> str = LongStream.range(0, numberOfChunks).mapToObj(i -> {
            try (FileChannel channel = new FileInputStream(piFile).getChannel()) {
                channel.position(i * CHUNK_SIZE);
                ByteBuffer buf = ByteBuffer.allocate(CHUNK_SIZE);
                channel.read(buf);
                return buf.array();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new byte[0];
        });
        return Multi.createFrom().items(str);

    }

}
