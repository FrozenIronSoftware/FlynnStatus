# FlynnStatus

A simple Flynn cluster health monitor that manages CloudFlare DNS records

# Installing

FlynnStatus is designed to run on Heroku, and any other system that
 supports Heroku buildpacks.
 
## Environment Variables

### MONITOR

Required

Type: json

A json array of servers to monitor.

Each entry in the array should be an object with the following fields:
- zone_id: CloudFlare Zone ID. This is available on the CloudFlare domain 
 overview page.
- dns_record_name: The **full** DNS record name. This will be used for
 requests to CloudFlare and to the Flynn cluster.
- dns_record_ip: The destination DNS record ip.
- flynn_status_key: Status key used to authenticate with the Flynn cluster.
 This can be found as an environment variable in the cluster's "status" app.
- cloudflare_api_key: CloudFlare API key found on the profile page.
- cloudflare_api_email: CloudFlare email used with the account API key

```json
[
  {
    "zone_id":"abc123",
    "dns_record_name":"flynn.example.com",
    "dns_record_ip":"8.8.8.8",
    "flynn_status_key":"def456",
    "cloudflare_api_key":"ghi789",
    "cloudflare_api_email": "foo@bar.com"
  }
]
```

### DRY_RUN

_Optional_

Type: boolean

If not set to a case-ignored false, No calls to modify DNS records on
 CloudFlare will be made. Alerters will still be called.
 
### LOG_LEVEL

_Optional_

Type: String

Default: INFO

Values: WARNING | INFO | FINE | FINER | FINEST | ALL

Defines the log level.

### MAILGUN_API_KEY

_Optional_

Type: string

If provided, mailgun will be used as an alerter service.

#### MAILGUN_FROM_EMAIL

Required if passing MAILGUN_API_KEY

Type: string

Format: `Foo <foo@bar.com>`

The email address to be used as the from address for messages. The domain
 should match the one managed by mailgun.
 
#### MAILGUN_TO_EMAIL

Required if passing MAILGUN_API_KEY

Type: string

Format: `Foo <foo@bar.com>`

The email address to be used as the to address for message.

# Using

Each defined node will has their [status page] polled once per minute. The
 cluster status defined by the endpoint will send an event to the [alerters],
 if the cluster is unhealthy, but it will not be used as a way of determining
 a node's health. 
 
A node's health will be determined unhealthy if the status page returns invalid
 json or does not finish a response within 30 seconds.

## Poll Interval

Servers to monitor will be polled once per minute.
 
Server status checks are done synchronously with a timeout of 30 seconds,
 so each node that does down will add 30 seconds to the poll interval.
 
## Alerters

Alerters are alert systems that will report critical failures and events.

### Implemented alerters

- Mailgun: Sends an email through the mailgun API

### Events

- Unhealthy Cluster: A cluster's status page has indicated it is unhealthy.
- CloudFlare API Fail: A request to the CloudFlare API has failed.
- DNS Record Removed: A DNS record has been removed from CloudFlare.
- DNS Record Added: A DNS record has been added to CloudFlare.

# License

FlynnStatus is a Flynn cluster and node health monitor that modifies
 CloudFlare DNS records. 

Copyright (C) 2018 Frozen Iron Software LLC

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.



[status page]: https://flynn.io/docs/production#monitoring
[alerters]: #alerters