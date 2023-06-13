import org.lwjgl.opengl.*;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Esfera3D {

    private long window;
    private int width = 800;
    private int height = 800;
    private float rotationAngle = 0.0f;

    public void run() {
        try {
            init();
            loop();
            GLFW.glfwDestroyWindow(window);
        } finally {
            GLFW.glfwTerminate();
        }
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("No se puede inicializar GLFW");
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = GLFW.glfwCreateWindow(width, height, "Esfera 3D", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("No se puede crear la ventana GLFW");
        }

        GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                GLFW.glfwSetWindowShouldClose(window, true);
            }
        });

        GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        GLFW.glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(window);
    }

    private void loop() {
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glEnable(GL_COLOR_MATERIAL);
        glEnable(GL_NORMALIZE);

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        while (!GLFW.glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            update();
            render();

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
    }

    private void update() {
        rotationAngle += 0.5f; // Ajusta la velocidad de rotación según tus necesidades
        if (rotationAngle >= 360.0f) {
            rotationAngle -= 360.0f;
        }
    }

    private void render() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glFrustum(-1.0, 1.0, -1.0, 1.0, 1.5, 20.0);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glTranslatef(0.0f, 0.0f, -5.0f);
        glRotatef(rotationAngle, 0.0f, 1.0f, 0.0f);

        // Luz
        float[] lightPosition = { 1.0f, 1.0f, 0.0f, 0.0f };
        glLightfv(GL_LIGHT0, GL_POSITION, lightPosition);

        // Material
        float[] ambient = { 0.2f, 0.2f, 0.2f, 1.0f };
        float[] diffuse = { 0.0f, 1.0f, 0.0f, 1.0f };
        float[] specular = { 1.0f, 1.0f, 1.0f, 1.0f };
        float shininess = 128.0f;

        glMaterialfv(GL_FRONT, GL_AMBIENT, ambient);
        glMaterialfv(GL_FRONT, GL_DIFFUSE, diffuse);
        glMaterialfv(GL_FRONT, GL_SPECULAR, specular);
        glMaterialf(GL_FRONT, GL_SHININESS, shininess);

        glColor3f(0.0f, 0.0f, 1.0f); // Verde
        drawSphere(1.0f, 64, 64);
    }

private void drawSphere(float radius, int stacks, int slices) {
    for (int i = 0; i < stacks; i++) {
        float stackAngle = (float) Math.PI / stacks * i;
        float stackAngleNext = (float) Math.PI / stacks * (i + 1);
        float stackY = radius * (float) Math.cos(stackAngle);
        float stackYNext = radius * (float) Math.cos(stackAngleNext);

        glBegin(GL_QUAD_STRIP);
        for (int j = 0; j <= slices; j++) {
            float sliceAngle = (float) Math.PI * 2 / slices * j;
            float sliceX = (float) (radius * Math.sin(stackAngle) * Math.cos(sliceAngle));
            float sliceZ = (float) (radius * Math.sin(stackAngle) * Math.sin(sliceAngle));
            float sliceXNext = (float) (radius * Math.sin(stackAngleNext) * Math.cos(sliceAngle));
            float sliceZNext = (float) (radius * Math.sin(stackAngleNext) * Math.sin(sliceAngle));

            float[] normal = { sliceX / radius, stackY / radius, sliceZ / radius };
            glNormal3fv(normal);
            glVertex3f(sliceX, stackY, sliceZ);

            normal = new float[] { sliceXNext / radius, stackYNext / radius, sliceZNext / radius };
            glNormal3fv(normal);
            glVertex3f(sliceXNext, stackYNext, sliceZNext);
        }
        glEnd();
        }
    }

    private void glNormal3fv(float[] normal) {
        glNormal3f(normal[0], normal[1], normal[2]);
    }

    public static void main(String[] args) {
        new Esfera3D().run();
    }
}