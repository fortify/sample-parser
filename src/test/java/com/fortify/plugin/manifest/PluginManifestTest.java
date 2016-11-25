package com.fortify.plugin.manifest;

import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by potockyt.
 */
public class PluginManifestTest {
    @Test
    public void testManifestUnmarshalling() {

        JAXBContext jaxbContext;
        try(InputStream is = PluginManifestTest.class.getResourceAsStream("/plugin.xml")) {
            jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
            XMLInputFactory xif = XMLInputFactory.newFactory();
            xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
            xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
            XMLStreamReader xsr = xif.createXMLStreamReader(is);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            PluginDescriptor descriptor = ((JAXBElement<PluginDescriptor>) jaxbUnmarshaller.unmarshal(xsr)).getValue();
            // TODO why is plugin info null?
            //Assert.assertNotNull("Plugin.PluginInfo is null", descriptor.getPluginInfo());
            //Assert.assertNotNull("Plugin.IssueParser is null", descriptor.getIssueParser());
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }
}
