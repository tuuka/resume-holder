package my.webapp.storage.serializer;

import my.webapp.exception.StorageException;
import my.webapp.model.Resume;

import java.io.*;

/* При сериализации, с помощью рефлексии объект (имплементирующий
интерфейс Serializable) разбирается по полям и пишется в поток.
При десериализации сперва выделяется память под объект, затем
заполняются его поля.
КОНСТРУКТОР СЕРИАЛИЗУЕМОГО ОБЪЕКТА НЕ ВЫЗЫВАЕТСЯ!
Поля родительского класса, унаследованные сериализуемым классом,
в поток сериализации не попадают, а при десериализации вызывается
конструктор родительского НЕсериализируемого объекта без параметров.
Если такого объекта нет, - возникнет ошибка!
(http://www.skipy.ru/technics/serialization.html)
*/

public class ObjectStreamSerializer implements ResumeSerializer{
    private String fileSuffix = ".obj";

    public ObjectStreamSerializer(){}

    public ObjectStreamSerializer(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    @Override
    public String getFileSuffix(){
        return fileSuffix;
    }

    @Override
    public void saveResume(Resume r, OutputStream os) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(r);
    }

    @Override
    public Resume loadResume(InputStream is) throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(is)) {
            return (Resume) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new StorageException("Error read resume!", e);
        }
    }
}
