Snapshot
=========

[1]: https://github.com/kylebalnave/semblance               "Semblance"
[2]: https://github.com/kylebalnave/disparity               "Disparity"
[3]: https://github.com/BBC-News/wraith                     "BBC-News Wraith"

A command line tool and [Semblance][1] Runner that creates Screenshots using Selenium WebDriver.
When used in conjunction with [Disparity][2], it can be used as an alternative to [BBC-News Wraith][3] 

### Commandline Usage

    java -jar dist/Snapshot.jar -config ./config.json
    
### Dependencies

The Semblance.jar [Semblance][1] should be included in the classpaths.

### Example Config

The below configuration will save screenshots of [BBC Homepage](http://www.bbc.co.uk/) and output a Junit Report

    {
        "threads": 5,
        "out": "./test/snaps/",
        "urls": [
            "http://www.bbc.co.uk/"
        ],
        "dimensions": [
            [1600, 1200]
        ],
        "drivers": [{
                "name": "firefox",
                "version": "",
                "hub": ""
            }],
        "reports": [
            {
                "className": "semblance.reporters.JunitReport",
                "out": "./test/reports/ss.junit"
            }
        ]
    }

### Config Explanation	

- threads

Number of parallel threads to use.  If Selenium Grid is not being used, this should be 5 or less depending on the WebDriver

- out

The folder to save snapshots in.

- urls

An array of valid urls to screenshot

- dimensions

An array of width, height pixel values to take snapshots at

- drivers

An array of driver details.  Firefox will work out of the box, but others may require setup.  Version and hub are optional!

- reports

Report details for all results
