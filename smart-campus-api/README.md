5C0SC022W Client Server Architectures
Smart Campus API : Coursework Report


Part 1: Service Architecture & Setup
     1.1: Project & Application Configuration 

     Everytime someone sends a request to the API, JAX-RS creates a brand new instance of the resources 
     class just to handle that one request.Once the resource class just to handle that one request.
     Once the response is sent, that instance is thrown away.This is the default behaviour 
     it is called per-request lifecycle.This sounds fine at first, but it creates a problem. 
     if each request gets its own fresh object,any data stored inside that object disappears after the request ends.
     So we cannot just store rooms or sensors as normal fields inside the resource class.
     To get around this, I used a single shared DataStore object that lives for the entire lifetime of the server. 
     All resource classes call DataStore.getInstance() to access the same maps. 
     Since, multiple requests can come in at the same time and all try to read or 
     write to these maps, I used ConcurrentHashMap instead of a regular HashMap.
     ConcurrentHashMap handles multiple threads safely without crashing or losing data.
    
     1.2: The "Discovery" Endpoint 
     
     HATEOAS stands for Hypermedia as the Engine of Application State. 
     In simple terms, it means that when the API gives you back a response, 
     it also tells you what you can do next by including lins to related endpoints. 
     For example, when you get a list of rooms, the response might also include a 
     link like '/api/v1/rooms/LIB-301/sensors'.This way, the client doesnot need to guess 
     or memorise the URL structure.It can just follow the links, like clicking hyperlinks on a webpage. 
     This is really useful for developers using the API. If the server changes a URL,
     clients that follow links will automatically adapt. Clients that hard coded the old URL wourld break. 
     It also means a new developer can explore the entire API just by looking at responses,
     without needing to read a separate manual.

Part 2: Room Management 
     2.1: Room Resource Implementation 
    
    If we only return IDs, the response is very small and fast to send. But the problem is that whoever is calling 
    the API now has to make a separate request for every single room just to find out its name and capacity. 
    if there are 200 rooms,that is 200 extra requests. This is slow and wasteful it is known as the N+1 problem.
    If we return the full room objects including the name, capacity, and sensor list. 
    The client gets everything it needs in one go. Yes, the response is a bit larger, but it saves a lot of back-and-forth.
    For a campus management system that needs to display a list of rooms with their details, this is clearly the better choice.
    If the number of rooms ever gets very large, the right solution would be to add pagination returning,say, 
    50 full room objects at a time rather than switching back to just IDs.

    2.2: Room Deletion & Safety Logic 
    
    Yes, it is idempotent. Idempotent basically means: no matter how many times you repeat the same action, the end result is the same.
    Here is what happens in the API: if you send a DELETE request for a room that exists, the room gets removed and you get back a 204 
    no Content response. If you then send the exact same DELETE request again, the room is already gone, so you get a 404 Not Found
    response instead. The response code is different, but the actual state of the server is the same both times the room does not exist.
    That is what makes it idempotent. This means it is completely safe for a client to retry a DELETE if they are not sure whether the first 
    one went through,without worrying about accidentally deleting something twice.

Part 3: Sensor Operations &  Linking
     3.1: Sensor Resource & Integrity
 
     The @Consumes(MediaType.APPLICATION_JSON) annotation tells JAX-RS that this endpoint only accepts JSON. So if a client sends the data as 
     plain text or XML instead, JAX- RS catches this JSON. So if a client sends the data as plain text or XML instead, 
     JAX-RS catches this immediately and sends back a 415 Unsupported Media Type error before your actaul Java method even runs.
     JAX-RS checks the Content Type header of every incoming request and compares it against what the method says it accepts.
     if they do not match, the request is blocked right there. This is useful because it means you do not have to write extra code inside your 
     method to check whether the data arrived in the right format. The framework handles it automatically.

     3.2: Filtered Retrieval & Search
    
     There are a few good reasons why using ? type = C02 as a query parameter is been than putting it in the path like /senors/type/C02.
     First, query parameteres are optional. The path /sensors on its own stil makes perfect sense and just returns all sensors.
     But if you build the the type into the path, the URL/sensors/type/without anything after it looks broken and confusing.
     Second, REST has a clear convention: the path is for identifying a specfic resource, and query parameteres are for filtering or 
     searching a collection. A sensor type is not identifying a unique resource, It is narrowing down a ist.So using @QueryParam is 
     the semantically correct choice. Third, it scales much better. if you later want to filter by both type and status, 
     you cant just add another parameter: ?type=C02&status =ACTIVE. Doing that with URL paths would get messy very quickly .

part 4: Deep Nesting with Sub - Resources
     4.1: The Sub Resource Location Pattern 

     
     Instead of putting all the code for /sensor/{id}/readings inside the same class that handles/sensors,
     the Sub-Resource Locator pattern lets you hand off that part of the URL to a completely seperate class.
     In the project, SensorResource has a method that, when it sees a request coming in for /readings, 
     creates a news SensorReadingResource object and passes the job to it. 
     The main benefit is that each class stays focused on one thing. SensorResource only worries about creating,
     listing, and finding sensors. SensorReadingResource only worries about reading history.
     Neither class becomes bloated with code that does not belong to it. It also makes thing much easier to test.
     You can text  SensorReadingResource completely on its own without needing to set up all of SensorResource fist. 
     And if you ever need to add more nested paths say,/sensors/{id}/alerts . 
     you create another class and add one more locator method. You do not have to touch any existing code.

part 5: Advanced Error Handling, Exception Mapping & Logging
     5.2: Dependency Validation 

     
     When a client tries to create a sensor and passes a roomId that does not exist,
     the URL they are calling /api/v1/sensors is perfectly fine. The endpoint exists. The JSON they sent is also vaild JSON.
     The problem is not with the URL or the format it is with the actual value inside the request body. 
     A 404 Not Found error would be misleading here because 404 means i could not find the page or endpoint you were looking for.'
     But the endpoint was found just fine. The issues is that a vlaue inside the played is pointing to something that does not exist.
     A 422 Unprocessable Entity is a much more honest answer. It tells the client: 'your request reached me, i understand it,
     the format is correct but i cannot process it because the data inside fails validation'. 
     That gives the developer a clear signal to look inside their request body, not at the URLthey used.

     5.4: The Global Safety Net
       
     A  Java stack trace looks like a wall of technical text, but it actually contains a surprising amount of using information for a attacker.
     For a start, it shows exactly which libraries and frameworks you are using, including their versionnumber.for example, jersey server2.34.
     An attacker can look up whether that version has any known security vulnerabilities and use them against your server.
     It also revels your internal package structure and class names, like com.smartcampus.resources.SensorResource. 
     This helps an attacker undertand how your application is laid out and target specfic parts of it. On top of that, 
     file paths sometimes appear in traces, showing where the application is deployed on the server. 
     And the sequence of method calls in a trace can expose internal business logic that the attacker could use to find weak spots.
     The fix is simple: log the full trace on the server side so developers can still debug problems, but only send a pain, 
     generic message to the client something like "An unexcpeted error occurred. Please conatct the administrator." The attacker gets nothing useful.

     5.5: API Request & Response Logging Filters

     The simple answer is: because it is much easier to maintain and impossible to forget. If you put a Logger .info()
     call inside every single resource method,you have to remember to add it every time you create a new andpoint.If someone forgets, 
     that endpoint has no logging.You also end up writing the same kind of code over and over in dozens of different places. 
     A JAX-RS filter solves all of this.You write the logging code once,in one class, and it automatically runs for every single
     request and response that passes through the API no matter how many endpoints you have or add in the future.
     It also keeps your resource methods clean.A method for creating a room should only contain code about creating a room. 
     Logging is infrastructure work that does not belong there. Keeping them separate makes the code easier to read, easier to test,
     and easier to change.
    

     

