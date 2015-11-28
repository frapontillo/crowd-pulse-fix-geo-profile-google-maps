crowd-pulse-fix-geo-profile-google-maps
=======================================

Google Maps based Crowd Pulse profile geo-location fixer.

---------------------------------------

To fix geolocation for profiles you need to create a `geocoding.properties` file and
put it into the resources directory (must be accessible by the class loader).

This file must contain the `geocoding.apiKey` property, whose value must be a 
Google Geocoding API key. To get your key, do the following:

1. If you don't have on already, create a new project on the [Google Developers Console]
(https://console.developers.google.com).
2. Go to "APIs & Auth".
3. Go to the "Credentials" sub-section and create a new Server Key. That's your key.
4. Go to the "APIs" sub-section and enable the "Geocoding API" from the Google Maps service.
  
## License

```
  Copyright 2015 Francesco Pontillo

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.

```