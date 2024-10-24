# Expense-tracker

# GOAL

A web service to track the expenses, dues, and settlements for college-going students.

## Tasks Completed

Following tasks have been completed:

1. User Register/login and authetication
2. Save expenses for logged in user
3. Get all expenses with updated payment status.
4. Find pending dues
5. Pay dues by the method of choice of user
   - FIFO - The expense which added first will get settled first
   - Latest repayment date first - the due that is nearest will be paid first
6. Get all Dues settlement requests history.

- APIs are exposed on URL [api](http://localhost:8080/)
- To view the API contracts, please visit [Api contracts](http://localhost:8080/swagger-ui/index.html#/)

## Platform

OS: Windows <br />
IDE : Visual Studio Code <br />

## Technology/Tools Used

1.  Spring Boot
2.  MongoDB
3.  Gradle
4.  Postman

## Pre-requisites

- Java, Gradle(Optional), IDE with lombok plugin
- Port:8080 is not in use

## Instructions

Please follow these instructions for setup and running the application.

Clone existing git repository by:

`git clone

Open the project in an IDE and Please wait for all depedencies to get downloaded.

if gradle installed

`./gradlew bootrun`

Otherwise,Please naviagate to

`expenseTracker\expenseTracker\src\main\java\com\example\expenseTracker\ExpenseTrackerApplication.java`

and run.

After the service is up, Please navigate to [Testing](http://localhost:8080/swagger-ui/index.html#/)

## Note

1. Please register with any valid email address, Invalid Email format is not supported. Refer list of invalid formats
   - username.@domain.com
   - .user.name@domain.com
   - user-name@domain.com.
   - username@.com
2. One time login with registered email Address is _mandatory_
   - A default email is present i.e.
3. Date format should be in `dd/mm/yyyy`
4. There is no option for deleting the data, please login with different email address to perform fresh set of operations.

## Steps

1.  Open [API contracts](http://localhost:8080/swagger-ui/index.html#)
2.  Register/login with a valid email address.
3.  Record an expense (Only 6 fields are enough from user POV)
    - A sample
      `{
   "itemName": "item1",
   "recipient" : "Amazon",
   "expenseAmount": 300.0,
   "paidAmount": 200.0,
   "paymentMethod": "Google pay",
   "dueDate" : "10/02/2000"
}`
4.  Find All dues for the user
5.  Settle dues by giving
    - A sample
      `{
  "amount": 500,
  "duePaymentStrategy": 1,
  "paymentGateway": "string"
}`
    - duePaymentStrategy
      - 1: First in first out - the due added first will be paid first
      - 2: Latest repayment date first - the due that is nearest will be paid first
6.  Get all dues Request history

## Assumptions

1. _No store preference_ - Settling the dues according to data entered and order provided irrespective of where it is spent.

## Limitations

1. Since the dues settlement request comes according to ordering of data(FIFO), updating the initial request for every dues settelment to make sure the one which comes first should go first.

   - Impact:
     - User will see the updated amount in the expense, while fetching it. For getting the history, have to fetch it from duesHistory api.

2. Input data validation is not happening
   - Impact:
     - If amount is greater than required dues amount then refunding the amount and for next request user has to give the money again(No wallet).

## Low Hanging Fruits

- Can use store id/store name to filter out data to get the dues remaining on a particular store
