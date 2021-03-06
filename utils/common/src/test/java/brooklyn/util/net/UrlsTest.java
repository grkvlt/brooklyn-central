package brooklyn.util.net;

import static org.testng.Assert.assertEquals;

import org.testng.Assert;
import org.testng.annotations.Test;

public class UrlsTest {

    @Test
    public void testUrlToUriToStringAndBack() {
        String u = "http://localhost:8080/sample";
        Assert.assertEquals(Urls.toUrl(u).toString(), u);
        Assert.assertEquals(Urls.toUri(u).toString(), u);
        Assert.assertEquals(Urls.toUri(Urls.toUrl(u)).toString(), u);
        Assert.assertEquals(Urls.toUrl(Urls.toUri(u)).toString(), u);        
    }
    
    @Test
    public void testMergePaths() throws Exception {
        assertEquals(Urls.mergePaths("a","b"), "a/b");
        assertEquals(Urls.mergePaths("/a//","/b/"), "/a/b/");
        assertEquals(Urls.mergePaths("foo://","/b/"), "foo:///b/");
        assertEquals(Urls.mergePaths("/","a","b","/"), "/a/b/");
    }

    @Test
    public void testIsUrlWithProtocol() {
        Assert.assertTrue(Urls.isUrlWithProtocol("http://localhost/"));
        Assert.assertTrue(Urls.isUrlWithProtocol("protocol:"));
        Assert.assertFalse(Urls.isUrlWithProtocol("protocol"));
        Assert.assertFalse(Urls.isUrlWithProtocol(":/"));
        Assert.assertFalse(Urls.isUrlWithProtocol("1:/"));
        Assert.assertFalse(Urls.isUrlWithProtocol(null));
    }

    @Test
    public void testGetBasename() {
        assertEquals(Urls.getBasename("http://somewhere.com/path/to/file.txt"), "file.txt");
        assertEquals(Urls.getBasename("http://somewhere.com/path/to/dir/"), "dir");
        assertEquals(Urls.getBasename("http://somewhere.com/path/to/file.txt?with/optional/suffice"), "file.txt");
        assertEquals(Urls.getBasename("filewith?.txt"), "filewith?.txt");
        assertEquals(Urls.getBasename(""), "");
        assertEquals(Urls.getBasename(null), null);
    }
    
}
