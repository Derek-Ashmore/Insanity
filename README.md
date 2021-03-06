# Insanity
Generic implementation of the 'retry' pattern.  The retry pattern is best used from a batch or asynchronous remote tasks (e.g. web service calls) that a user/caller isn't waiting for.  It's purpose is to compensate for temporary outages automatically, without the need for human intervention.

In case you didn't get the project title, insanity is repeating something and expecting a different result.

## System Requirements
* Java JDK 6.0 or above (it was compiled under JDK 7 using 1.6 as the target source).
* Apache Commons Lang version 3.0 or above  

## Installation Instructions  
Insanity is easy to install whether you use maven or not.

### Maven Users  
Maven users can find dependency information [here](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22Insanity%22).

### Non-Maven Users  
Include the following jars in your class path:  
* Download the Insanity jar from [Github](https://github.com/Derek-Ashmore/Insanity/releases) and put it in your class path.  
* Insure Apache Commons Lang version 3.0 or above is in your class path.  

## Usage Instructions
To use Insanity, follow these instructions:  
* Create a "callable" that calls your remote resource
* Use the RetryManager class to execute your callable.

This RetryManager has a pluggable retry algorithm (just implement interface RetryAlgorithm).  The default algorithm will
retry up to a given number of times (default is 5) and will sleep for a defined period between retries (default is 5 minutes).  

If the max number of retries is reached without success, a RetryException will be generated.  The cause of that exception will be the last exception thrown by the callable.

## Usage examples:

### A most basic example with the default algorithm
```  
RetryManager<String> retryManager = new RetryManager<String>();  
MyCallable callable = new MyCallable();  

String callResult = retryManager.invoke(callable);
```  

### An example configuring the retry algorithm  
```  
DefaultRetryAlgorithm algorithm = new DefaultRetryAlgorithm(2, 10L);
RetryManager<String> retryManager = new RetryManager<String>(algorithm);  
MyCallable callable = new MyCallable();  

String callResult = retryManager.invoke(callable);
```  

## Creating your own retry algorithm
Implement interface RetryAlgorithm.  See DefaultRetryAlgorithm as an example.  

You are required to implement the following methods:

```  
public boolean isExecutionAllowed();  
public void reportExecutionFailure(Exception exceptionThrown);  
public void reset();    

```  

