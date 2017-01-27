package com.thirdparty.scan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * (C) Copyright 2015,2016 Hewlett Packard Enterprise Development, L.P.
 */
public class ScanGenerator {

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        if (args.length != 4) {
            System.err.println(String.format("Usage: %s OUTPUT.json ISSUE_COUNT CATEGORY_COUNT LONG_TEXT_SIZE", ScanGenerator.class.getSimpleName()));
            System.exit(1);
        }
        final File outputFile = new File(args[0]);
        final int issueCount = Integer.valueOf(args[1]);
        final int categoryCount = Integer.valueOf(args[2]);
        final int longTextSize = Integer.valueOf(args[3]);
        final Random r = SecureRandom.getInstanceStrong();
        final Instant now = Instant.now();

        final List<Finding> findings = new ArrayList<>(issueCount);
        for (int i = 0; i < issueCount; i++) {
            final Finding f = new Finding();
            f.setUniqueId(UUID.randomUUID().toString());
            final String id = String.format("%s/%08d", f.getUniqueId(), i + 1);
            f.setCategory(String.format("Category %d", r.nextInt(categoryCount)));
            f.setFileName(String.format("file-%s.bin", id));
            f.setVulnerabilityAbstract("Abstract for vulnerability " + id);
            f.setLineNumber(r.nextInt(Integer.MAX_VALUE));
            f.setConfidence(r.nextFloat() * 9 + 1); // 1..10
            f.setBuildServer("server1.example.com");
            f.setArtifact(String.format("artifact-%s.jar", id));
            f.setText1("Very long text 1 for " + id + getLoremIpsum(longTextSize));
            f.setText2("Very long text 2 for " + id + getLoremIpsum(longTextSize));
            f.setRatio(BigDecimal.valueOf(r.nextFloat() + 200.0));
            f.setBuildNumber(BigDecimal.valueOf(r.nextFloat() + 300.0));
            f.setLastChangeDate(Date.from(now.minus(2, ChronoUnit.DAYS).minus(2, ChronoUnit.HOURS)));
            f.setArtifactBuildDate(Date.from(now.minus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.HOURS)));
            findings.add(f);
        }

        final Scan s = new Scan();
        s.setScanDate(new Date());
        s.setEngineVersion("1.0-SNAPSHOT");
        s.setElapsed(r.nextInt(24 * 3600) + 1);
        s.setHostName(Inet4Address.getLocalHost().getHostName());
        s.setFindings(findings);

        try (
            final OutputStream out = new FileOutputStream(outputFile);
            final ZipOutputStream zipOut = new ZipOutputStream(out);
        ) {
            writeScanInfo("SAMPLE", zipOut);
            writeScan(s, zipOut);
        } catch (final Exception e) {
            try {
                Files.delete(outputFile.toPath());
            } catch (final Exception suppressed) {
                e.addSuppressed(suppressed);
            }
            throw e;
        }
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

    private static void writeScan(final Scan scan, final ZipOutputStream zipOut) throws IOException {
        final ObjectWriter writer = new ObjectMapper().writerFor(Scan.class);
        zipOut.putNextEntry(new ZipEntry("sample-scan.json"));
        writer.writeValue(zipOut, scan);
    }

    private static String getLoremIpsum(final int maxSize) {
        final StringBuilder sb = new StringBuilder("\n");
        for (int i = LOREM_IPSUM.length(); i < maxSize; i += LOREM_IPSUM.length()) {
            sb.append(LOREM_IPSUM);
        }
        return sb.toString();
    }

    private static String LOREM_IPSUM =
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
        "Everti vivendum splendide ad qui, ad quod nominavi comprehensam quo, mollis scripta eu eum. Te pro dicta volumus, his affert ornatus dissentias id. Mea no quot referrentur, an his eius eripuit noluisse. His eu legere eruditi.";
}
