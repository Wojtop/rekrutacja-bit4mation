import { useCallback, useState } from "react";

export const useCenteredTree = (defaultTranslate = { x: 0, y: 0 }) => {
  const [translate, setTranslate] = useState(defaultTranslate);
  const containerRef = useCallback((containerElem) => {
    if (containerElem !== null) {
      const { width, height } = containerElem.getBoundingClientRect();
      setTranslate({ x: width / 2, y: height / 5 });
    }
  }, []);
  return [translate, containerRef];
};

export function translateData(treeResponse) {
  console.log(treeResponse)
  // let references = Object.values(treeResponse.references);
  let references = new Map();
  treeResponse.references.forEach(element => {
    references.set(element.id, element.value)
  });
  // let references = new Map(Object.entries(treeResponse.references));
  let relations = new Map(Object.entries(treeResponse.relations));
  let leafs = new Map(Object.entries(treeResponse.leafs));

  console.log("references: ", references)
  console.log("relations: ", relations)
  console.log("leafs: ", leafs)

  let visualTreeElems = new Map();
  references.forEach((value, key, map) => {
    // console.log("In map foreach. Key: ", key, " Value: ", value)
    visualTreeElems.set(key.toString(), {name: value.toString(), attributes: {"id": key.toString(),}, children: []})
  })
  console.log("Visual tree elems: ", visualTreeElems)
  let childrenIds = [];   // list of all childrens ID. ID which is not here is a root

  relations.forEach((relationChildren, parentId, map) => {
    childrenIds.push(...relationChildren)
    console.log("relation children: ", relationChildren)
    relationChildren.forEach(childId => {
      let parent = visualTreeElems.get(parentId.toString())
      // console.log("Parent Id: ", parentId," Parent: ",parent)
      parent.children.push(visualTreeElems.get(childId.toString()))
    })
  })
  leafs.forEach((sum, lastNodeId, map) => {
        visualTreeElems.get(lastNodeId.toString()).children.push({name: sum.toString(), children: []})
      }
  )

  console.log("Visual tree: ", visualTreeElems)
  let possibleRoots = []
  visualTreeElems.forEach((v, key, map) => {
    if (!childrenIds.includes(parseInt(key))) {
      possibleRoots.push(key)
    }
  })
  console.log("Possible roots: ", possibleRoots)
  console.log("returned data: ", visualTreeElems.get(possibleRoots.toString()))
  return visualTreeElems.get(possibleRoots.toString());
}

const serverAdress = 'localhost'
const serverPort = 8080
export const getData = async (setData, setError, setLoading) => {
  callApi('GET',`http://${serverAdress}:${serverPort}/tree/getTree`, setData, setError, setLoading)
  // fetch(`http://${serverAdress}:${serverPort}/tree/getTree`)
  //     .then((response) => {
  //       if (response.ok) {
  //         return response.json(); // Parse the response data as JSON
  //       } else {
  //         throw new Error('API request failed');
  //       }
  //     })
  //     .then((actualData) => {
  //       actualData = translateData(actualData)
  //       console.log("Tree data: ", actualData)
  //       setData(actualData);
  //       setError(null);
  //     })
  //     .catch((err) => {
  //       setError(err.message);
  //       setData(null);
  //     })
  //     .finally(() => {
  //       setLoading(false);
  //       console.log("Loading set to false")
  //     });
  // console.log("Data: ", data)
}

export const callApi = async (method, url, setData, setError, setLoading)=>{
  fetch(url, {method: method})
      .then((response) => {
        if (response.ok) {
          return response.json(); // Parse the response data as JSON
        } else {
          throw new Error('API request failed');
        }
      })
      .then((actualData) => {
        actualData = translateData(actualData)
        console.log("Tree data: ", actualData)
        setData(actualData);
        setError(null);
      })
      .catch((err) => {
        setError(err.message);
        setData(null);
      })
      .finally(() => {
        setLoading(false);
        console.log("Loading set to false")
      });
}

export const callDeleteNode = async (nodeId, setData, setError, setLoading) =>{
  callApi('DELETE', `http://${serverAdress}:${serverPort}/tree/deleteNode/${nodeId}`, setData, setError, setLoading)
}

