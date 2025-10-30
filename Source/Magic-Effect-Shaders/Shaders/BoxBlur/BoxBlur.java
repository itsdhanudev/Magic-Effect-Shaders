package JAVARuntime;

import java.util.*;

public class BoxBlur extends MaterialShader {

  public Color color = new Color(255, 255, 255);
  public Texture texture;
  public float strength = 4.0f;
  public float resolution = 512.0f;
  public float radius = 2.0f;
  public float intensity = 1.0f;
  public float iterations = 1.0f;
  public float offset = 1.0f;

  private Shader shader;

  @Override
  public String getShaderName() {
    return "CustomShaders/BoxBlur";
  }

  @Override
  public float getMinimalSupportedOGL() {
    return MaterialShader.OGL3;
  }

  @Override
  void start() {
    Shader.Builder builder = new Shader.Builder();
    builder.createProgram();

    VertexShader vs = VertexShader.loadFile(this, "BoxBlurVertex");
    FragmentShader fs = FragmentShader.loadFile(this, "BoxBlurFragment");

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
    ogl.uniformFloat("u_Radius", radius);
    ogl.uniformFloat("u_Intensity", intensity);
    ogl.uniformFloat("u_Iterations", iterations);
    ogl.uniformFloat("u_Offset", offset);

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