package JAVARuntime;

import java.util.*;
import java.text.*;
import java.net.*;
import java.math.*;
import java.io.*;
import java.nio.*;
import java.nio.*;
import java.util.HashMap;
import java.util.Map;

/* @Author Dhanu */
public class Test extends Component {
  public Camera cam;
  public FrameBuffer fb;

  public ModelRenderer mr1, mr2;
  /// Run only once
  void start() {}

  /// Repeat every frame
  void repeat() {
    fb = cam.getFrameBuffer();
    print(fb.getColorTextureArray().length);
    print(fb.getColorAttachmentArray().length);
    setTex(fb.getColorTexture(0),mr1);
    setTex(fb.getColorAttachment(0),mr2);
  }

  public void setTex(Texture tex, ModelRenderer mr) {
      mr.getMaterial().setAlbedo(tex);
  } 
}
