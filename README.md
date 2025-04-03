# StockMarketApp

StockMarketApp

In order to run this project, you will need Docker service installed.

After you install Docker, open terminal and navigate to the root of project.

When you are in root folder, type next two commands one by one:

- docker build -t stock_marketplace .

- docker-compose up -d

This should get whole project up and running

In case you are not able to install the Docker service, there are few things required to install.

At first you will need Java 23 installed.

Next you will need MySQL Server on port 3306, and MySQL Workbench or some other software for db administration.

Open Workbench and connect to server with root username and password.

Execute next queries:

CREATE DATABASE IF NOT EXISTS marketplace DEFAULT CHARACTER SET = 'utf8' DEFAULT COLLATE 'utf8_bin'
CREATE USER 'stock_marketplace'@'%' IDENTIFIED BY 'stock_marketplace';
GRANT ALL PRIVILEGES ON marketplace.\* TO 'stock_marketplace'@'%';
FLUSH PRIVILEGES;

In root folder of project is the dump of database. In workbench you have to open Administration tab, choose data import option, choose self-contained file option, and select dump.sql file from project's root folder.

When you install those two software packages, execute queries and import data, you need to open terminal and navigate to root folder of project.

When you are in root folder of project type next command:

- java -jar stock_marketplace-0.0.1-SNAPSHOT.jar

First thing that you will need is to log in with username and password.

url: http://localhost:8080/api/auth/login
method: POST
data:
{
"username":"john_smith92",
"password":"qW1rI7uPzR7D"
}

If you want to register new user:
url: http:://localhost:8080/api/auth/register
method: POST
data:
{
"username":"username",
"password":"password"
}

This will return you one user data and token. That is the access token that you must use from now on.

In any next request, you will have to set HTTP request header like this:

Authorization:Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huX3NtaXRoOTIiLCJleHAiOjE3NDM2NjY5NzEsImlhdCI6MTc0MzYzMDk3MX0.T--H7yrUIoG8jl66hPc6xS_i0fqA6j-Mv4FMjKNHoTI

Only for websockets you won't need authorization header.

Here are some example API urls that you can try:

if you want to see global order book, top 10 active selling and buying orders:

url: http://localhost:8080/api/user/order/getOrderBook
method: GET

if you want to see shares that you own:

url: http://localhost:8080/api/user/share/getMyShares
method: GET

if you want to see all companies:

url: http://localhost:8080/api/user/company/getAllCompanies
method: GET

if you want to see open orders for specific company:

url: http://localhost:8080/api/user/order/getOpenOrdersForCompany/2 - 2 is company id
method: GET

url: http://localhost:8080/api/user/order/placeOrder -place order, eather to buy or to sell shares for specific company
method:POST
data:
{
"company":{"id":"2"},
"orderOption":"buy", --buy or sell
"buyingPrice":"140",
"quantity":"2"
}

this will put your order in stack of orders for shares of company that you want
if you are willing to sell shares, it will find a match that is willing to buy shares for identical price
if you are willing to buy shares, it will find a match that is willing to sell shares for identical price

after you submit your order, you can connect to a websocket, and wait to see if it will find a match
after it finds match, you will get message and be disconnected from socket

url: ws://localhost:8080/ws/orders/2/2
the first parameter is id of company that you are trying to buy/sell shares for
the second parameter is your, user id

if you want to see list of your orders:

url: http://localhost:8080/api/user/order/getMyOrders
method: GET

if you want to cancel your order:

url: http://localhost:8080/api/user/order/cancelOrder
method: POST
data:
{
"id":"2" -id of your order
}
