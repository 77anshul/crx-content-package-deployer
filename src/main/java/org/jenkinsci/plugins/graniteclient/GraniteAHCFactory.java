/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <http://unlicense.org/>
 */

package org.jenkinsci.plugins.graniteclient;

import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.common.AbstractIdCredentialsListBoxModel;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Realm;
import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;
import jenkins.plugins.asynchttpclient.AHCUtils;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Global extension and configurable factory for {@link AsyncHttpClient} instances
 */
@Extension
public final class GraniteAHCFactory extends Descriptor<GraniteAHCFactory> implements Describable<GraniteAHCFactory> {

    private static final AsyncHttpClientConfig DEFAULT_CONFIG = new AsyncHttpClientConfig.Builder().build();

    private String credentialsId;
    private int connectionTimeoutInMs = DEFAULT_CONFIG.getConnectionTimeoutInMs();
    private int idleConnectionTimeoutInMs = DEFAULT_CONFIG.getIdleConnectionTimeoutInMs();
    private int requestTimeoutInMs = DEFAULT_CONFIG.getRequestTimeoutInMs();

    public GraniteAHCFactory() {
        super(GraniteAHCFactory.class);
        load();
    }

    @SuppressWarnings("unchecked")
    public Descriptor<GraniteAHCFactory> getDescriptor() {
        return Jenkins.getInstance().getDescriptorOrDie(getClass());
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        req.bindJSON(this, json.getJSONObject("GraniteAHCFactory"));
        save();
        return true;
    }

    public String getCredentialsId() {
        return credentialsId;
    }

    public void setCredentialsId(String credentialsId) {
        this.credentialsId = credentialsId;
    }

    public int getConnectionTimeoutInMs() {
        return connectionTimeoutInMs;
    }

    public void setConnectionTimeoutInMs(int connectionTimeoutInMs) {
        this.connectionTimeoutInMs = connectionTimeoutInMs;
    }

    public int getIdleConnectionTimeoutInMs() {
        return idleConnectionTimeoutInMs;
    }

    public void setIdleConnectionTimeoutInMs(int idleConnectionTimeoutInMs) {
        this.idleConnectionTimeoutInMs = idleConnectionTimeoutInMs;
    }

    public int getRequestTimeoutInMs() {
        return requestTimeoutInMs;
    }

    public void setRequestTimeoutInMs(int requestTimeoutInMs) {
        this.requestTimeoutInMs = requestTimeoutInMs;
    }

    @Override
    public String getDisplayName() {
        return "CRX Content Package Deployer - HTTP Client";
    }

    public AbstractIdCredentialsListBoxModel doFillCredentialsIdItems() {
        return GraniteCredentialsListBoxModel.fillItems();
    }

    public Credentials getDefaultCredentials() {
        if (this.credentialsId != null) {
            return GraniteNamedIdCredentials.getCredentialsById(this.credentialsId);
        } else {
            return null;
        }
    }

    public AsyncHttpClient newInstance() {
        return new AsyncHttpClient(
                new AsyncHttpClientConfig.Builder()
                        .setProxyServer(AHCUtils.getProxyServer())
                        .setConnectionTimeoutInMs(this.connectionTimeoutInMs)
                        .setIdleConnectionTimeoutInMs(this.idleConnectionTimeoutInMs)
                        .setRequestTimeoutInMs(this.requestTimeoutInMs)
                        .build());
    }

    public static GraniteAHCFactory getFactoryInstance() {
        Descriptor descriptor = Jenkins.getInstance().getDescriptorOrDie(GraniteAHCFactory.class);
        if (descriptor instanceof GraniteAHCFactory) {
            return (GraniteAHCFactory) descriptor;
        } else {
            return new GraniteAHCFactory();
        }
    }
}
