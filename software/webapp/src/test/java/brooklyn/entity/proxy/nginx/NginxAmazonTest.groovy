package brooklyn.entity.proxy.nginx

import static brooklyn.test.TestUtils.*
import static org.testng.Assert.*

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import brooklyn.entity.Application
import brooklyn.entity.basic.Entities
import brooklyn.entity.group.DynamicCluster
import brooklyn.entity.webapp.JavaWebAppService
import brooklyn.entity.webapp.WebAppService
import brooklyn.entity.webapp.jboss.JBoss7Server
import brooklyn.location.Location
import brooklyn.location.MachineLocation
import brooklyn.management.ManagementContext
import brooklyn.test.entity.TestApplication

import com.google.common.collect.ImmutableMap

/**
 * Test Nginx proxying a cluster of JBoss7Server entities on AWS for ENGR-1689.
 *
 * This test is a proof-of-concept for the Brooklyn demo application, with each
 * service running on a separate Amazon EC2 instance.
 */
public class NginxAmazonTest {
    private static final Logger LOG = LoggerFactory.getLogger(NginxAmazonTest.class)
    
    Application app
    NginxController nginx
    DynamicCluster cluster
    Location loc

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        ManagementContext managementContext = Entities.newManagementContext(
            ImmutableMap.of("brooklyn.location.jclouds.aws-ec2.image-id", "us-east-1/ami-2342a94a"));
        
        loc = managementContext.getLocationRegistry().resolve("aws-ec2:us-east-1")
        app = new TestApplication()
        Entities.startManagement(app, managementContext)
    }

    @AfterMethod(alwaysRun = true)
    public void shutdown() {
        if (app != null) Entities.destroyAll(app);
    }
    
    @Test(groups = "Live")
    public void testProvisionAwsCluster() {
        def template = { Map properties -> new JBoss7Server(properties) }
        
        cluster = new DynamicCluster(parent:app, factory:template, initialSize:2, httpPort:8080 )
        URL war = getClass().getClassLoader().getResource("swf-booking-mvc.war")
        assertNotNull war, "Unable to locate resource $war"
        cluster.setConfig(JavaWebAppService.ROOT_WAR, war.path)
        cluster.start([ loc ])

        nginx = new NginxController([
                "parent" : app,
                "cluster" : cluster,
                "domain" : "localhost",
                "port" : 8000,
                "portNumberSensor" : WebAppService.HTTP_PORT,
            ])

        nginx.start([ loc ])
        
        executeUntilSucceeds {
            // Nginx URL is available
            MachineLocation machine = nginx.locations.find { true }
            String url = "http://" + machine.address.hostName + ":" + nginx.getAttribute(NginxController.HTTP_PORT) + "/swf-booking-mvc"
            assertTrue urlRespondsWithStatusCode200(url)

            // Web-app URL is available
            cluster.members.each {
                assertTrue urlRespondsWithStatusCode200(it.getAttribute(JavaWebAppService.ROOT_URL) + "swf-booking-mvc")
            }
        }

		nginx.stop()
    }
}
