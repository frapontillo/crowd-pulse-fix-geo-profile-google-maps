/*
 * Copyright 2015 Francesco Pontillo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.frapontillo.pulse.crowd.fixgeoprofile.googlemaps;

import com.github.frapontillo.pulse.crowd.data.entity.Profile;
import com.github.frapontillo.pulse.crowd.fixgeoprofile.IProfileGeoFixerOperator;
import com.github.frapontillo.pulse.spi.IPlugin;
import com.github.frapontillo.pulse.spi.VoidConfig;
import com.github.frapontillo.pulse.util.PulseLogger;
import com.github.frapontillo.pulse.util.StringUtil;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import rx.Observable;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of an {@link IPlugin} that accepts and streams {@link Profile}s after attempting
 * a geo-location fix on them, using the Google Maps reverse geocoding APIs.
 * <p/>
 * To use this plugin, create a {@code geocoding.properties} file in the classpath with a {@code
 * geocoding.apiKey} property set to your Google Maps API key.
 *
 * @author Francesco Pontillo
 */
public class GoogleMapsProfileGeoFixer extends IPlugin<Profile, Profile, VoidConfig> {
    public final static String PLUGIN_NAME = "googlemaps";
    private final static String PROP_GEOCODING_APIKEY = "geocoding.apiKey";
    private final GeoApiContext context;

    public GoogleMapsProfileGeoFixer() {
        context = new GeoApiContext().setApiKey(readApiKey())
                // if the request doesn't succeed within 10 seconds, discard it
                .setRetryTimeout(10, TimeUnit.SECONDS);
    }

    @Override public String getName() {
        return PLUGIN_NAME;
    }

    @Override public VoidConfig getNewParameter() {
        return new VoidConfig();
    }

    @Override protected Observable.Operator<Profile, Profile> getOperator(VoidConfig parameters) {
        return new IProfileGeoFixerOperator(this) {
            @Override public Double[] getCoordinates(Profile profile) {
                if (StringUtil.isNullOrEmpty(profile.getLocation())) {
                    return null;
                }
                GeocodingResult[] results = null;
                Double[] coordinates = null;
                // attempt a forward geocoding (from address to lat-lng)
                try {
                    results =
                            GeocodingApi.newRequest(context).address(profile.getLocation()).await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // edit and notify the profile only if lat-lng coordinates were found
                if (results != null && results.length > 0) {
                    coordinates = new Double[]{results[0].geometry.location.lat,
                            results[0].geometry.location.lng};
                }
                return coordinates;
            }
        };
    }

    /**
     * Read the API key from the {@code geocoding.properties} file in the {@code geocoding.apiKey}
     * property.
     *
     * @return The Google Maps API key.
     */
    private String readApiKey() {
        InputStream configInput =
                getClass().getClassLoader().getResourceAsStream("geocoding.properties");
        Properties prop = new Properties();
        try {
            prop.load(configInput);
            return prop.getProperty(PROP_GEOCODING_APIKEY);
        } catch (Exception exception) {
            PulseLogger.getLogger(GoogleMapsProfileGeoFixer.class)
                    .error("Error while loading Google Maps configuration", exception);
            return "";
        }
    }
}
