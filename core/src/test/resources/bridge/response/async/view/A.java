/*
 * Copyright 2013 eXo Platform SAS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bridge.response.async.view;

import juzu.Response;
import juzu.View;
import juzu.impl.common.Tools;
import juzu.io.AsyncStreamable;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class A {

  @View
  public Response.Content index() throws IOException {
    final AsyncStreamable content = new AsyncStreamable();

    HttpServletRequest req = CurrentRequest.req.get();
    AsyncContext async = req.startAsync();
    Runnable task = new Runnable() {
      public void run() {
        try {
          Thread.sleep(500);
          content.append("pass");
        }
        catch (Exception e) {
          e.printStackTrace();
        }
        finally {
          Tools.safeClose(content);
        }
      }
    };
    try {
      async.start(task);
    }
    catch (RuntimeException e) {
      e.printStackTrace();
      throw e;
    }
    return Response.content(200, content);
  }
}
