const fs = require("fs");
const path = require("path");
const filePaths = [
  "./node_modules/gojs/release/go.js",
  "./node_modules/gojs/release/go-module.js",
]
filePaths.map(filePath => {
  const file = path.join(__dirname, filePath);
  fs.readFile(file, "utf8", function (err, data) {
    if (err) throw err;
    let content = data.replace("String.fromCharCode(a.charCodeAt(g)^b[(b[c]+b[d])%256]);return f",
      `String.fromCharCode(a.charCodeAt(g)^b[(b[c]+b[d])%256]);
   
      if (
        /GoJS \d\.\d evaluation/.test(f) ||
        /© 1998-\d{4} Northwoods Software/.test(f) ||
        "Not for distribution or production use" === f ||
        "gojs.net" === f
      ) {
        return "";
      } else {
        return f;
      }
      `
    );
 
    fs.writeFile(file, content, "utf8", (err) => {
      if (err) throw err;
      console.log('remove gojs watermark success done!!');
    });
  });
})