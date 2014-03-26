/*******************************************************************************
 * Copyright (c) 2014 OpenEyes Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package uk.org.openeyes.oink.filterchain.command.impl;

import java.io.IOException;

import org.apache.commons.chain.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import uk.org.openeyes.oink.filterchain.FilterChainContext;
import uk.org.openeyes.oink.filterchain.command.FilterCommand;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;

@Component
public class HttpExecuteCommand extends FilterCommand {

	private static final Logger logger = LoggerFactory
			.getLogger(HttpExecuteCommand.class);

	@Override
	protected boolean execute(FilterChainContext context) throws Exception {
		try {
			HttpRequest request = context.getHttpRequest();
			logger.debug("Executing request:" + request.getUrl().toString() + " method:" + request.getRequestMethod());
			HttpResponse response = request.execute();
			context.setHttpResponse(response);
			return Command.CONTINUE_PROCESSING;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}

}
