package au.edu.bond.classgroups.config;

import au.edu.bond.classgroups.config.model.Configuration;
import au.edu.bond.classgroups.config.model.PullFileCsvFeedDeserialiserConfig;
import au.edu.bond.classgroups.config.model.PullUrlCsvFeedDeserialiserConfig;
import au.edu.bond.classgroups.config.model.PushCsvFeedDeserialiserConfig;
import au.edu.bond.classgroups.model.Schedule;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.ClassAliasingMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loads and persists the configuration from and into an XML file in Blackboard's shared content.
 * Created by Shane Argo on 23/05/14.
 */
public class XmlConfigurationService extends ConfigurationService {

    @Autowired
    private File configurationXMLFile;
    @Autowired
    private File configurationXMLLockFile;


    private int configurationPollingFreq;

    /**
     * Loads the configuration from the central configuration file on Blackboard's shared content.
     * @return The loaded configuration
     */
    @Override
    public Configuration loadConfiguration() {
        FileChannel fileChannel = null;
        FileLock fileLock = null;
        InputStream inputStream = null;
        XStream xstream = buildConfigXStream();

        if(!configurationXMLFile.exists()) {
            return new Configuration();
        }

        Configuration configuration = null;
        try {
            blockWhileLocked();
            fileChannel = new RandomAccessFile(configurationXMLFile, "rw").getChannel();
            fileLock = fileChannel.lock();
            inputStream = Channels.newInputStream(fileChannel);
        } catch (IOException ex) {
            Logger.getLogger(XmlConfigurationService.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(XmlConfigurationService.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }

        try {
            configuration = (Configuration) xstream.fromXML(inputStream);
        } catch(XStreamException ex) {
            Logger.getLogger(XmlConfigurationService.class.getName()).log(Level.SEVERE, "Failed to decode XStream", ex);
        } finally {
            if(fileLock != null) {
                try {
                    if(fileLock.isValid()) {
                        fileLock.release();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(XmlConfigurationService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    // If the stream closes dirtily, then its hardly worth failing over, just log it.
                    Logger.getLogger(XmlConfigurationService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if(fileChannel != null) {
                try {
                    fileChannel.close();
                } catch (IOException ex) {
                    Logger.getLogger(XmlConfigurationService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return configuration;
    }

    /**
     * Persists the configuration into the central configuration file on Blackboard's shared content.
     * @param configuration The configuration to be persisted.
     */
    @Override
    public void persistConfiguration(Configuration configuration) {
        FileChannel fileChannel = null;
        FileLock fileLock = null;
        OutputStream outputStream = null;
        XStream xstream = buildConfigXStream();

        try {
            lock();

            fileChannel = new RandomAccessFile(configurationXMLFile, "rw").getChannel();
            fileLock = fileChannel.lock();
            fileChannel.truncate(0);
            outputStream = Channels.newOutputStream(fileChannel);
            xstream.toXML(configuration, outputStream);
        } catch (IOException ex) {
            Logger.getLogger(XmlConfigurationService.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(String.format("Failed to open configuration file for writing: %s", configurationXMLFile.getAbsolutePath()), ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(XmlConfigurationService.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(String.format("Interrupted while opening configuration file for writing: %s", configurationXMLFile.getAbsolutePath()), ex);
        } finally {
            unlock();
            if(fileLock != null) {
                try {
                    if(fileLock.isValid()) {
                        fileLock.release();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(XmlConfigurationService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if(outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    // If the stream closes dirtily, then its hardly worth failing over, just log it.
                    Logger.getLogger(XmlConfigurationService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if(fileChannel != null) {
                try {
                    fileChannel.close();
                } catch (IOException ex) {
                    Logger.getLogger(XmlConfigurationService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void blockWhileLocked() throws InterruptedException {
        while(configurationXMLLockFile.exists()) {
            Thread.sleep(100);
        }
    }

    private void lock() throws IOException, InterruptedException {
        blockWhileLocked();
        configurationXMLLockFile.createNewFile();
        configurationXMLLockFile.deleteOnExit();
    }

    private void unlock() {
        configurationXMLLockFile.delete();
    }

    /**
     * Constructs the XStream object for parsing the configuration XML. This essentially describes the structure of the
     * XML
     * @return An XStream which describes the XML structure.
     */
    private XStream buildConfigXStream() {
        XStream xstream = new XStream(new DomDriver("UTF-8"));
        xstream.alias("configuration", Configuration.class);
        xstream.alias("pullFileCsvFeedDeserialiser", PullFileCsvFeedDeserialiserConfig.class);
        xstream.alias("pullUrlCsvFeedDeserialiser", PullUrlCsvFeedDeserialiserConfig.class);
        xstream.alias("pushCsvFeedDeserialiser", PushCsvFeedDeserialiserConfig.class);

        ClassAliasingMapper mapper = new ClassAliasingMapper(xstream.getMapper());
        mapper.addClassAlias("tool", String.class);
        xstream.registerLocalConverter(Configuration.class, "defaultTools", new CollectionConverter(mapper));

        mapper.addClassAlias("schedule", Schedule.class);
        xstream.registerLocalConverter(Configuration.class, "schedules", new CollectionConverter(mapper));

        return xstream;
    }

    @Override
    public ConfigurationMonitor getMonitor() {
        return new PollingConfigurationMonitor(configurationPollingFreq, configurationXMLFile, this);
    }

    public File getConfigurationXMLFile() {
        return configurationXMLFile;
    }

    public void setConfigurationXMLFile(File configurationXMLFile) {
        this.configurationXMLFile = configurationXMLFile;
    }

    public File getConfigurationXMLLockFile() {
        return configurationXMLLockFile;
    }

    public void setConfigurationXMLLockFile(File configurationXMLLockFile) {
        this.configurationXMLLockFile = configurationXMLLockFile;
    }

    public int getConfigurationPollingFreq() {
        return configurationPollingFreq;
    }

    public void setConfigurationPollingFreq(int configurationPollingFreq) {
        this.configurationPollingFreq = configurationPollingFreq;
    }
}
