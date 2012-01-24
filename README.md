# TfL Countdown for BlackBerry

### Background

Transport for London provide a service at http://countdown.tfl.gov.uk/ that provides realtime estimates of bus arrival times at all of London's bus stops. There is a full web interface, a mobile web interface and an accessible web interface available for this on the TfL website.

At present there is no published API, although this is due out early this year (apparently). There is however an unpublished API (the details of which can easily be observed by using the web interface and then watching the JSON traffic).

There are Android and iPhone applications already that provide the Countdown service in an 'app' form, but sadly nothing for us BlackBerry stalwarts. This is an *extremely* basic BlackBerry version that took an afternoon to put together. There's lots missing, but it does the job for me.

### API usage

Please note that the TfL bus departures API is undocumented, unpublished and they have granted nobody permission to use it. It can change any time. Short story: Use it at your own risk.

### Features

Not many at the moment...

1. Lookup live departures information by bus stop code (printed on each bus stop, and also available online)
2. Lists route number, destination and estimated wait time in the results
3. Lists any additional messages (such as diversions, system outages, etc)
4. Allows stop codes and a description to be saved as favourites

### Installing

If you don't want to build your own version from source, download the files from the "bin" directory above and install them onto your device via the BlackBerry desktop manager.

### Building

I've built this with BlackBerry JDE 4.5 (download it from BlackBerry.com). This means it will run on any device with OS 4.5 or above (an old 8800 will run 4.5). I suspect it may also compile on 4.2 (8700 devices), but I haven't tried this.

You will also need signing keys from RIM if you want to run it on real devices. You can request these free of charge from RIM.

To build: Just open the BlackBerry project and press build. Make sure you sign it after building.

To deploy: Either use the Desktop Manager application with the .cod and .jar files, or instead copy the .jad, .cod and .jar files to a web server and point your BB browser at the .jad file. If it's your own web server, ensure you have a mime-type configured for the .jad extension.

### Bugs

1. The front screen allows you to scroll even when there is not enough content to warrant scrolling.
2. The '[123]' input hint covers up the loading message depending on the font size set.

### Limitations / To do list

1. No bus stop search functionality. Searching by road / postcode would probably be useful.
2. No location awareness, e.g. via GPS/WiFi/etc. Goes hand-in-hand with #1 above.
