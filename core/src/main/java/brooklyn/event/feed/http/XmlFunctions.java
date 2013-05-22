/*
 * Copyright 2013 by Cloudsoft Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package brooklyn.event.feed.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import brooklyn.util.exceptions.Exceptions;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;

public class XmlFunctions {
    private static final Logger log = LoggerFactory.getLogger(XmlFunctions.class);

    private XmlFunctions() {} // instead use static utility methods

    /**
     * Parses an XML string as a {@link Document} object.
     */
    public static Function<String, Document> asDocument() {
        return new Function<String, Document>() {
            @Override
            public Document apply(String input) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                byte[] buffer = input.getBytes(Charsets.UTF_8);
                InputStream data = new ByteArrayInputStream(buffer, 0, buffer.length);
                try {
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.parse(data);
                    return doc;
                } catch (Exception e) {
                    throw Exceptions.propagate(e);
                }
            }
        };
    }

    /**
     * Applies the XPath expression provided by the {@link Supplier} to the input XML {@link Document} object.
     *
     * @see #xpath(String)
     */
    public static Function<Document, String> xpath(final Supplier<String> expressionSupplier) {
        return new Function<Document, String>() {
            @Override
            public String apply(@Nullable Document input) {
                return xpath(expressionSupplier.get()).apply(input);
            }
        };
    }

    /**
     * Applies the given XPath expression to the input XML {@link Document} object.
     */
    public static Function<Document, String> xpath(final String expression) {
        return new Function<Document, String>() {
            @Override
            public String apply(Document input) {
                XPathFactory xPathfactory = XPathFactory.newInstance();
                XPath xpath = xPathfactory.newXPath();
                try {
                    XPathExpression expr = xpath.compile(expression);
                    return expr.evaluate(input);
                } catch (XPathExpressionException e) {
                    log.warn("Could not evaluate '{}' against input: {}", expression, e.getMessage());
                    throw Exceptions.propagate(e);
                }
            }
        };
    }

    /**
     * Logs the input text at {@literal DEBUG} level.
     *
     * @return the output is identical to the input
     */
    public static Function<String, String> debug(final String message) {
        return new Function<String, String>() {
            @Override
            public String apply(String input) {
                if (log.isDebugEnabled()) {
                    log.debug("{}: {}", message, input);
                }
                return input;
            }
        };
    }

    /**
     * Cast text to an expected class, returning a default value on error or an empty input.
     * 
     * @see #cast(Class)
     */
    public static <T> Function<String, T> cast(final Class<T> expected, final T defaultValue) {
        return new Function<String, T>() {
            @Override
            public T apply(@Nullable String input) {
                try {
                    return Preconditions.checkNotNull(cast(expected).apply(input));
                } catch (Exception e) {
                    return defaultValue;
                }
            }
        };
    }

    /**
     * Casts the text contents of an XML element or attribute to the expected class.
     * <p>
     * Returns {@literal null} if the input is the empty string or is null.
     *
     * @throws IllegalArgumentException if the input cannot be cast to the expected class
     * @see #cast(Class, Object)
     */
    public static <T> Function<String, T> cast(final Class<T> expected) {
        return new Function<String, T>() {
            @SuppressWarnings("unchecked")
            @Override
            public T apply(String input) {
                if (Strings.isNullOrEmpty(input)) {
                    return (T) null;
                } else if (expected == boolean.class || expected == Boolean.class) {
                    return (T) Boolean.valueOf(input);
                } else if ((expected == char.class || expected == Character.class) && input.length() == 1) {
                    return (T) Character.valueOf(input.charAt(0));
                } else if (expected == byte.class || expected == Byte.class) {
                    return (T) Byte.valueOf(input);
                } else if (expected == short.class || expected == Short.class) {
                    return (T) Short.valueOf(input);
                } else if (expected == int.class || expected == Integer.class) {
                    return (T) Integer.valueOf(input);
                } else if (expected == long.class || expected == Long.class) {
                    return (T) Long.valueOf(input);
                } else if (expected == float.class || expected == Float.class) {
                    return (T) Float.valueOf(input);
                } else if (expected == double.class || expected == Double.class) {
                    return (T) Double.valueOf(input);
                } else if (expected == BigDecimal.class) {
                    return (T) new BigDecimal(input);
                } else if (expected == BigInteger.class) {
                    return (T) new BigInteger(input);
                } else if (expected == String.class) {
                    return (T) input;
                } else {
                    throw new IllegalArgumentException("Cannot cast XML string to type "+expected);
                }
            }
        };
    }

    /**
     * Ignores the input and returns the default value for the expected class.
     * <p>
     * Only supports primitives and {@link Number} sub-classes.
     *
     * @see #defaultValueSupplier(Class)
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> Function<String, T> defaultValue(final Class<T> expected) {
        return (Function) Functions.forSupplier(defaultValueSupplier(expected));
    }

    public static <T> Supplier<T> defaultValueSupplier(final Class<T> expected) {
        return new Supplier<T>() {
            @SuppressWarnings("unchecked")
            @Override
            public T get() {
                if (expected == boolean.class || expected == Boolean.class) {
                    return (T) Boolean.FALSE;
                } else if (expected == char.class || expected == Character.class) {
                    return (T) Character.valueOf('\0');
                } else if (expected == byte.class || expected == Byte.class) {
                    return (T) Byte.valueOf((byte) 0x0);
                } else if (expected == short.class || expected == Short.class) {
                    return (T) Short.valueOf((short) 0);
                } else if (expected == int.class || expected == Integer.class) {
                    return (T) Integer.valueOf(0);
                } else if (expected == long.class || expected == Long.class) {
                    return (T) Long.valueOf(0l);
                } else if (expected == float.class || expected == Float.class) {
                    return (T) Float.valueOf(0f);
                } else if (expected == double.class || expected == Double.class) {
                    return (T) Double.valueOf(0d);
                } else if (expected == BigDecimal.class) {
                    return (T) new BigDecimal(0);
                } else if (expected == BigInteger.class) {
                    return (T) new BigInteger("0");
                } else {
                    throw new IllegalArgumentException("Unknown XML default value for type "+expected);
                }
            }
        };
    }
}
