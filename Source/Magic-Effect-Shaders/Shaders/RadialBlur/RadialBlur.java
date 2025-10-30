package JAVARuntime;

import java.util.*;

public class RadialBlur extends MaterialShader {

  public Color color = new Color(255, 255, 255);
  public Texture texture;
  public float strength = 4.0f;
  public float resolution = 512.0f;
  public float iterations = 8.0f;
  public float centerX = 0.5f;
  public float centerY = 0.5f;
  public float intensity = 1.0f;
  public float falloff = 1.0f;
  public float radius = 1.0f;

  private Shader shader;

  @Override
  public String getShaderName() {
    return "CustomShaders/RadialBlur";
  }

  @Override
  public float getMinimalSupportedOGL() {
    return MaterialShader.OGL3;
  }

  @Override
  void start() {
    Shader.Builder builder = new Shader.Builder();
    builder.createProgram();

    VertexShader vs = VertexShader.loadFile(this, "RadialBlurVertex");
    FragmentShader fs = FragmentShader.loadFile(this, "RadialBlurFragment");

    builder.setVertexCode(vs);
    builder.setFragmentCode(fs);

    builder.compileVertex();
    builder.compileFragment();
    shader = builder.create();
  }

  @Override
  void render(OGLES ogles, Camera camera, MSRenderData renderData) {
    if (shader == null) return;

    OGLES3 ogl = (OGLES3) ogles;
    ogl.setIgnoreAttributeException(true);
    ogl.withShader(shader);

    ogl.uniformMatrix4("viewMatrix", camera.getViewMatrix());
    ogl.uniformMatrix4("projectionMatrix", camera.getProjectionMatrix());
    ogl.uniformColor("diffuse", color);
    ogl.uniformFloat("u_Strength", strength);
    ogl.uniformFloat("u_Resolution", resolution);
    ogl.uniformFloat("u_Iterations", iterations);
    ogl.uniformFloat("u_CenterX", centerX);
    ogl.uniformFloat("u_CenterY", centerY);
    ogl.uniformFloat("u_Intensity", intensity);
    ogl.uniformFloat("u_Falloff", falloff);
    ogl.uniformFloat("u_Radius", radius);

    if (Texture.isRenderable(texture)) {
      ogl.uniformTexture("u_MainTex", texture);
    } else {
      ogl.uniformTexture("u_MainTex", Texture.white());
    }

    for (int rv = 0; rv < renderData.vertexCount(); rv++) {
      RenderableVertex rVertex = renderData.renderableVertexAt(rv);
      Vertex vertex = rVertex.vertex;

      if (rVertex.objectCount() > 0) {
        applyVertexAttributes(vertex, ogl);

        for (int ro = 0; ro < rVertex.objectCount(); ro++) {
          RenderableObject rObject = rVertex.objectAt(ro);
          if (rObject.isVisibleByCamera()) {
            ogl.uniformMatrix4("modelMatrix", rObject.getRenderMatrix());
            ogl.drawTriangles(vertex.getTrianglesBuffer());
          }
        }
      }
    }

    ogl.releaseAttributes();
    ogl.releaseShader();
  }

  private void applyVertexAttributes(Vertex vertex, OGLES ogl) {
    if (vertex.getVerticesBuffer() != null) ogl.attributeVector3("position", vertex.getVerticesBuffer());
    if (vertex.getUVsBuffer() != null) ogl.attributeVector2("texCoord", vertex.getUVsBuffer());
  }
}