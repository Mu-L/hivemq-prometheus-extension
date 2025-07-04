/*
 * Copyright 2018-present HiveMQ GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hivemq.extensions.prometheus.export;

import com.codahale.metrics.MetricRegistry;
import com.hivemq.extensions.prometheus.configuration.PrometheusExtensionConfiguration;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PrometheusServerTest {

    private final @NotNull PrometheusExtensionConfiguration config = mock(PrometheusExtensionConfiguration.class);

    @BeforeEach
    void setUp() {
        final var port = createRandomPort();
        when(config.hostIp()).thenReturn("localhost");
        when(config.port()).thenReturn(port);
        when(config.metricPath()).thenReturn("/metrics");
    }

    @Test
    void test_start_stop_successful() throws Exception {
        final var prometheusServer = new PrometheusServer(config, new MetricRegistry());
        prometheusServer.start();
        //noinspection HttpUrlsUsage
        final var url = new URL("http://" + config.hostIp() + ":" + config.port() + config.metricPath());
        final var con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        assertThat(con.getResponseCode()).isEqualTo(200);
        prometheusServer.stop();
    }

    private int createRandomPort() {
        try {
            final var serverSocket = new ServerSocket(0);
            final var port = serverSocket.getLocalPort();
            serverSocket.close();
            return port;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
