package shared;
import java.io.Serializable;
public record BallRecord(Point pos, int vX, int vY) implements Serializable{}
