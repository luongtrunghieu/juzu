/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package juzu.request;

import juzu.impl.plugin.application.Application;
import juzu.impl.request.Method;
import juzu.impl.request.Request;
import juzu.impl.bridge.spi.ResourceBridge;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class ResourceContext extends MimeContext {

  /** . */
  private ResourceBridge bridge;

  public ResourceContext(Request request, Application application, Method method, ResourceBridge bridge) {
    super(request, application, method);

    //
    this.bridge = bridge;
  }

  public ClientContext getClientContext() {
    return bridge.getClientContext();
  }

  @Override
  protected ResourceBridge getBridge() {
    return bridge;
  }

  @Override
  public Phase.Resource getPhase() {
    return Phase.RESOURCE;
  }
}
