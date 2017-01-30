## About

SMS Relay is an open-source app which transforms your Android device into
an incoming SMS gateway. It does this by evaluating each incoming SMS against
sender/content filters and forwarding it to a HTTP server if matched. Due to
real-time nature, it can be used wherever the SMS content is of interest to
the server-side application e.g. for OTP consumption or remote server control
e.g. ChatOps.

## Download

Get it from the [Play Store](https://play.google.com/store/apps/details?id=com.advarisk.smsrelay),
it is less than 20kB and requires minimal permissions.

## Configuration

By default, SMS Relay is disabled. On enabling it, you can configure the
following settings:

| Setting                   | Description
| ------------------------- | ----------------------------------------------------------
| URL                       | URL to which the payload will be uploaded
| HTTP Method               | Either `PUT` (default) or `POST`
| HTTP Basic Authentication | Credentials to identify device to server
| Filter by Sender          | Messages not from specified senders will be blocked
| Filter by Content         | Messages not containing text from any line will be blocked

To ensure that nobody can upload random data to the URL configured above, it
is recommended to use `PUT` as the HTTP Method (which is the default) and use
HTTP Basic Authentication with every allowed device being assigned a unique
username and/or password. If either the username or password is blank, then
the `Authorization` HTTP header is not included in the request.

Configuring both the sender and content filter is highly recommended to
ensure that only relevant messages get forwarded, saving bandwidth and
avoiding messages potentially containing private conversations and/or
notifications being sent to the server.

## HTTP Server

The HTTP server is expected to accept the configured HTTP method for the
specified URL, and return HTTP `403` in case the credentials are incorrect.
In case the credentials are accepted, return HTTP `200` to indicate that
the message was accepted. The server will receive two parameters:

| Parameter   | Description
| ---------   | -----------
| `sender`    | the phone number if sent from regular phone, or [display name](https://developer.android.com/reference/android/telephony/SmsMessage.html#getDisplayOriginatingAddress()) if sent from gateway.
| `content`   | raw [message content](https://developer.android.com/reference/android/telephony/SmsMessage.html#getDisplayMessageBody()).
| `recipient` | recipient [phone number](https://developer.android.com/reference/android/telephony/SubscriptionInfo.html#getNumber()) if available, or the [display name](https://developer.android.com/reference/android/telephony/SubscriptionInfo.html#getDisplayName()).

The `recipient` is available only on Android 5.1 (API 22) and above, it
will be blank on lower versions due to APIs not being available.

## Privacy

SMS Relay does not use or transmit any information to anyone. It only
transmits the SMS sender/recipent and content for messages which match
the filters to the user-specified URL (i.e. one-way communication).

## Support

Rate it on the Google Play page with feedback, and we'll try to respond.
