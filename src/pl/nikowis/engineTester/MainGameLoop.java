package pl.nikowis.engineTester;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import pl.nikowis.entities.Camera;
import pl.nikowis.entities.Entity;
import pl.nikowis.entities.Light;
import pl.nikowis.entities.Player;
import pl.nikowis.models.RawModel;
import pl.nikowis.models.TexturedModel;
import pl.nikowis.renderEngine.DisplayManager;
import pl.nikowis.renderEngine.Loader;
import pl.nikowis.renderEngine.MasterRenderer;
import pl.nikowis.renderEngine.OBJLoader;
import pl.nikowis.terrains.Terrain;
import pl.nikowis.textures.ModelTexture;
import pl.nikowis.textures.TerrainTexture;
import pl.nikowis.textures.TerrainTexturePack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Nikodem on 12/22/2016.
 */
public class MainGameLoop {

    public static void main(String[] args) {

        DisplayManager.createDisplay();
        Loader loader = new Loader();

        RawModel rawTreeModel = OBJLoader.loadObjModel("tree", loader);
        TexturedModel staticTreeModel = new TexturedModel(rawTreeModel, new ModelTexture(loader.loadTexture("tree")));

        RawModel rawPersonModel = OBJLoader.loadObjModel("person", loader);
        TexturedModel staticPersonModel = new TexturedModel(rawPersonModel, new ModelTexture(loader.loadTexture("playerTexture")));

        List<Entity> entities = new ArrayList<Entity>();
        Random random = new Random();
        for (int i = 0; i < 500; i++) {
            entities.add(new Entity(staticTreeModel, new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600), 0, 0, 0, 3));
        }


        Player player = new Player(staticPersonModel, new Vector3f(0, 0, -50), 0, 0, 0, 1);

        Light light = new Light(new Vector3f(20000, 20000, 2000), new Vector3f(1, 1, 1));


        //####################################################################
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        //####################################################################

        Terrain terrain = new Terrain(-1, -1, loader, texturePack, blendMap);
        Terrain terrain2 = new Terrain(0, -1, loader, texturePack, blendMap);

        Camera camera = new Camera(player);
        MasterRenderer renderer = new MasterRenderer();

        ModelTexture texture = staticTreeModel.getTexture();
        texture.setShineDamper(100);
        texture.setReflectivity(1);

        while (!Display.isCloseRequested()) {
            camera.move();
            player.move();
            renderer.processEntity(player);
            renderer.processTerrain(terrain);
            renderer.processTerrain(terrain2);
            for (Entity entity : entities) {
                renderer.processEntity(entity);
            }

            renderer.render(light, camera);
            DisplayManager.updateDisplay();
        }

        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
