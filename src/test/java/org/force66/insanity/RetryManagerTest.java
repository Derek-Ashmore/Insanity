/*
 * This software is licensed under the Apache License, Version 2.0
 * (the "License") agreement; you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.force66.insanity;

import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RetryManagerTest {
	
	RetryManager<String> retryManager;
	MyCallable callable;

	@Before
	public void setUp() throws Exception {
		retryManager = new RetryManager<String>();
		callable=new MyCallable();
	}

	@Test
	public void testConstructors() throws Exception {
		retryManager = new RetryManager<String>();
		DefaultRetryAlgorithm algorithm = (DefaultRetryAlgorithm)retryManager.getRetryAlgorithm();
		Assert.assertTrue(algorithm != null);
		Assert.assertTrue(DefaultRetryAlgorithm.DEFAULT_MAX_NBR_RETRIES.equals(algorithm.getMaxNbrRetries()));
		Assert.assertTrue(DefaultRetryAlgorithm.DEFAULT_TIME_BETWEEN_RETRIES_IN_MILLIS.equals(algorithm.getTimeBetweenRetriesInMillis()));
		
		retryManager = new RetryManager<String>(algorithm);
		Assert.assertTrue(algorithm == retryManager.getRetryAlgorithm());
		
		try {
			retryManager = new RetryManager<String>(null);
			Assert.fail("Null constructor should have failed.");
		}
		catch (Exception e) {
			Assert.assertTrue(e.getMessage().contains("Null algorithm not allowed"));
		}
	}
	
	@Test
	public void testBasic() throws Exception {
		DefaultRetryAlgorithm algorithm = new DefaultRetryAlgorithm(2, 10L);
		retryManager = new RetryManager<String>(algorithm);
		
		Assert.assertTrue("stuff".equals(retryManager.invoke(callable)));
		
		callable.throwException = true;
		testException("invocation not successful");
		testException("invocation not successful");
		
		callable.throwException = false;
		Assert.assertTrue("stuff".equals(retryManager.invoke(callable)));

	}
	
	private void testException(String testMessage) {
		Exception exceptionThrown=null;
		try {retryManager.invoke(callable);}
		catch (Exception e) {
			exceptionThrown=e;
		}
		Assert.assertTrue(exceptionThrown != null);
		Assert.assertTrue(exceptionThrown.getMessage().contains(testMessage));
	}
	
	static class MyCallable implements Callable<String>{
		
		boolean throwException=false;

		public String call() throws Exception {
			if (throwException) {
				throw new Exception("crap");
			}
			return "stuff";
		}
		
	}

}
