## About

SMS Relay is an open-source app which transforms your Android device into
an incoming SMS gateway. It does this by evaluating each incoming SMS against
sender/content filters and forwarding it to a HTTP server if matched. Due to
real-time nature, it can be used wherever the SMS content is of interest to
the server-side application e.g. for OTP consumption or remote server control
e.g. ChatOps.

## Download

Get it from the [Play Store](https://play.google.com/store/apps/details?id=com.advarisk.smsrelay),
it is less than 20kB and requires minimal permissions (SMS and Internet).

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
specified URL, and return HTTP `401` in case the credentials are incorrect.
In case the credentials are accepted, return HTTP `200` to indicate that
the message was accepted. The message will receive two parameters:

| Parameter | Description
| --------- | ---------------
| [`sender`](https://developer.android.com/reference/android/telephony/SmsMessage.html#getDisplayOriginatingAddress())  | the phone number if sent from regular phone, or display name if sent from gateway.
| [`content`](https://developer.android.com/reference/android/telephony/SmsMessage.html#getDisplayMessageBody()) | raw message content.

It is currently not possible to identify the recipent's number in case of
multi-SIM devices, as this information is not available via standard APIs.

## Support

Rate it on the Google Play page with feedback, and we'll try to respond.
