## make my url short

api info

POST /api/shorten

Success: 200

Invalid URL: 400

Custom Code taken: 409

Body format: code, short url, long url, expiring time

GET /api/{code}

Found: 302

Not Found: 404

Expired: 410

Error Response Body: timestamp, status, message, path



 

