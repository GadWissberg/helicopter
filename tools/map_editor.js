const DEFAULT_SIZE = 48
const ID_MAP = "map"
function tableCreate() {
  const body = document.body;
  tbl = document.getElementById(ID_MAP).appendChild(document.createElement('table'));

  for (let i = 0; i < DEFAULT_SIZE; i++) {
    const tr = tbl.insertRow();
    for (let j = 0; j < DEFAULT_SIZE; j++) {
        const td = tr.insertCell();
//        td.appendChild(document.createTextNode("&nbsp;"));
    }
  }

}

tableCreate();