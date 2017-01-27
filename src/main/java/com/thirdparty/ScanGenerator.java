package com.thirdparty;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.thirdparty.scan.DateSerializer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * (C) Copyright 2015,2016 Hewlett Packard Enterprise Development, L.P.
 */
public class ScanGenerator {

    private static final DateSerializer DATE_SERIALIZER = new DateSerializer();
    private static final Charset charset = StandardCharsets.US_ASCII;

    private final Random random;
    private final File outputFile;
    private final int issueCount;
    private final int categoryCount;
    private final int longTextSize;
    private final Instant now;

    private ScanGenerator(final Random random, final File outputFile, final int issueCount, final int categoryCount, final int longTextSize, final Instant now) {
        this.random = random;
        this.outputFile = outputFile;
        this.issueCount = issueCount;
        this.categoryCount = categoryCount;
        this.longTextSize = longTextSize;
        this.now = now;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InterruptedException {
        if (args.length != 4) {
            System.err.println(String.format("Usage: %s OUTPUT_SCAN.zip ISSUE_COUNT CATEGORY_COUNT LONG_TEXT_SIZE", ScanGenerator.class.getSimpleName()));
            System.exit(1);
        }
        final ScanGenerator scanGenerator = new ScanGenerator(SecureRandom.getInstanceStrong(), new File(args[0]), Integer.valueOf(args[1]), Integer.valueOf(args[2]), Integer.valueOf(args[3]), Instant.now());
        scanGenerator.write();
    }

    private void write() throws IOException, InterruptedException {
        if (!outputFile.createNewFile()) {
            System.err.println(String.format("File %s already exists!", outputFile.getPath()));
            System.exit(2);
        }
        try (
            final OutputStream out = new FileOutputStream(outputFile);
            final ZipOutputStream zipOut = new ZipOutputStream(out);
        ) {
            writeScanInfo("SAMPLE", zipOut);
            writeScan(zipOut);
        } catch (final Exception e) {
            try {
                Files.delete(outputFile.toPath());
            } catch (final Exception suppressed) {
                e.addSuppressed(suppressed);
            }
            throw e;
        }
        System.out.println(String.format("Scan file %s successfully created.", outputFile.getPath()));
    }

    private static void writeScanInfo(final String engineType, final ZipOutputStream zipOut) throws IOException {
        final Properties scanInfoProps = new Properties();
        scanInfoProps.put("engineType", engineType);
        try (final ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
            scanInfoProps.store(byteOut, "scan.info");
            zipOut.putNextEntry(new ZipEntry("scan.info"));
            zipOut.write(byteOut.toByteArray());
        }
    }

    private void writeScan(final ZipOutputStream zipOut) throws IOException, InterruptedException {
        final long startTime = System.currentTimeMillis();
        zipOut.putNextEntry(new ZipEntry("sample-scan.json"));
        try (final JsonGenerator jsonGenerator = new JsonFactory().createGenerator(zipOut)) {
            jsonGenerator.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("scanDate", DATE_SERIALIZER.convert(new Date()));
            jsonGenerator.writeStringField("engineVersion", "1.0-SNAPSHOT");
            jsonGenerator.writeStringField("hostName", Inet4Address.getLocalHost().getHostName());
            jsonGenerator.writeArrayFieldStart("findings");
            int i;
            for (i = 0; i < issueCount; i++) {
                writeFinding(jsonGenerator, i);
            }
            jsonGenerator.writeEndArray();
            // NB: this value should be in seconds, but we always want some non-zero value, so we use millis
            jsonGenerator.writeNumberField("elapsed", (System.currentTimeMillis() - startTime));
            jsonGenerator.writeEndObject();
        }
    }

    private void writeFinding(final JsonGenerator jsonGenerator, final int i) throws IOException, InterruptedException {
        final String uniqueId = UUID.randomUUID().toString();
        final String id = String.format("%s/%08d", uniqueId, i + 1);
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("uniqueId", uniqueId);
        jsonGenerator.writeStringField("category", String.format("Category %d", random.nextInt(categoryCount)));
        jsonGenerator.writeStringField("fileName", String.format("file-%s.bin", id));
        jsonGenerator.writeStringField("vulnerabilityAbstract", "Abstract for vulnerability " + id);
        jsonGenerator.writeNumberField("lineNumber", random.nextInt(Integer.MAX_VALUE));
        jsonGenerator.writeNumberField("confidence", random.nextFloat() * 9 + 1); // 1..10
        jsonGenerator.writeStringField("buildServer", "server1.example.com");
        jsonGenerator.writeStringField("artifact", String.format("artifact-%s.jar", id));
        jsonGenerator.writeFieldName("text1");
        writeLoremIpsum("Very long text1 for " + id + '\n', jsonGenerator);
        jsonGenerator.writeFieldName("text2");
        writeLoremIpsum("Very long text2 for " + id + '\n', jsonGenerator);
        jsonGenerator.writeNumberField("ratio", BigDecimal.valueOf(random.nextFloat() + 200.0));
        jsonGenerator.writeNumberField("buildNumber", BigDecimal.valueOf(random.nextFloat() + 300.0));
        jsonGenerator.writeStringField("lastChangeDate", DATE_SERIALIZER.convert(Date.from(now.minus(2, ChronoUnit.DAYS).minus(2, ChronoUnit.HOURS))));
        jsonGenerator.writeStringField("artifactBuildDate", DATE_SERIALIZER.convert(Date.from(now.minus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.HOURS))));
        jsonGenerator.writeEndObject();
    }

    private void writeLoremIpsum(final String name, final JsonGenerator jsonGenerator) throws InterruptedException, IOException {
        final int size = longTextSize + name.length();
        try (final InputStream in = getLoremIpsum(name, size)) {
            jsonGenerator.writeBinary(in, size);
        }
    }

    private static InputStream getLoremIpsum(final String name, final int size) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final PipedInputStream in = new PipedInputStream();
        try {
            final Thread t = new Thread(() -> pipedStreamProducer(name, in, latch, size));
            t.setDaemon(true);
            t.start();
            if (latch.await(10, TimeUnit.SECONDS)) {
                return in;
            } else {
                t.interrupt();
                throw new RuntimeException("Timeout while waiting for latch for " + name);
            }
        } catch (final Exception e) {
            try {
                in.close();
            } catch (final Exception suppressed) {
                e.addSuppressed(suppressed);
            }
            throw e;
        }
    }

    private static void pipedStreamProducer(final String name, final PipedInputStream in, final CountDownLatch latch, final int size) {
        try (final PipedOutputStream out = new PipedOutputStream(in)) {
            latch.countDown();
            int written = min(name.length(), size);
            out.write(name.getBytes(charset), 0, written);
            final int loremIpsumLen = LOREM_IPSUM.length;
            while (written < size) {
                final int len = min(loremIpsumLen, size - written);
                out.write(LOREM_IPSUM, 0, len);
                written += len;
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private static int min(final int i, final int j) {
        return i < j ? i : j;
    }

    private static byte[] LOREM_IPSUM = (
        "Lorem ipsum dolor sit amet, eam ridens cetero iuvaret id. Ius eros fabulas ei. Te vis unum intellegam, cu sed ullum eruditi, et est lorem volumus. Te altera malorum quaestio mei, sea ea veniam disputando.\n" +
        "\n" +
        "Illud labitur definitionem ut sit, veri illum qui ut. Ludus patrioque voluptaria pri ad. Magna mundi voluptatum his ea. His paulo possim ea, et vide omittam philosophia sit. Eu lucilius legendos incorrupte eos, eu falli molestie argumentum cum.\n" +
        "\n" +
        "Melius torquatos ea his. Movet dolorem cu eam. Nisl offendit repudiare ne est. No veri appareat petentium eum.\n" +
        "\n" +
        "Duo in omnium accumsan legendos. Pro id probo oportere salutatus, sonet omnium epicurei eu pri. Indoctum disputando ei sea, an viris legere delicatissimi vix, ne dico melius admodum eam. Pro nostro inimicus liberavisse an. Id pro nostrum theophrastus, his et liber iusto docendi, purto convenire tincidunt pri an.\n" +
        "\n" +
        "Ex ubique accusamus est. Te sumo persecuti mei. Ne veniam mollis mei, natum perfecto definitionem at has. Liber honestatis ad cum, porro expetendis conclusionemque per eu. Suscipit dissentiet per an, ad usu sumo homero debitis. At eam quando placerat, nonumy forensibus scripserit at pro.\n" +
        "\n" +
        "Vero quodsi no usu, usu nisl erat iracundia in. Sed te habeo viris graeco. Persius admodum sententiae no eam, ut dicunt erroribus sit. Dolorum appetere legendos et qui. Vim ei feugait perfecto sadipscing.\n" +
        "\n" +
        "Nam audire detracto et, epicurei suscipiantur at his, vis id veri dolor. Pro id insolens singulis, accumsan singulis eam at, qui cu diam ceteros singulis. Atqui graecis fastidii cu mei. Invidunt singulis ex eam, et detracto hendrerit sadipscing quo. Est ne graeci vidisse placerat, wisi appareat erroribus ius an. Ea vim dicam aperiri. Ex elit aliquid est, nostro intellegam mel te.\n" +
        "\n" +
        "Eu per consul semper vituperatoribus, odio dicat audiam eam ea. Qui ei iisque nonumes repudiare, fugit quidam eu sit. An eam debet concludaturque, in nostro meliore splendide quo, ei est eros accumsan scribentur. Ei idque dolore honestatis sea. Tollit convenire salutatus ex mea, quem tantas epicurei in usu.\n" +
        "\n" +
        "Nam at cibo nominati, ne meis harum per, eu cum brute saepe veniam. Quo fabulas insolens cu, vix ne animal detraxit. Adhuc paulo similique ut eam, cu sit persius phaedrum. Cu eruditi periculis salutatus est, dicam veniam verterem ius at.\n" +
        "\n" +
        "Everti vivendum splendide ad qui, ad quod nominavi comprehensam quo, mollis scripta eu eum. Te pro dicta volumus, his affert ornatus dissentias id. Mea no quot referrentur, an his eius eripuit noluisse. His eu legere eruditi."
    ).getBytes(charset);
}
