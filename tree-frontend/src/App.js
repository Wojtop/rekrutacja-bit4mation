import React from "react";
import Tree from "react-d3-tree";
import "./App.css";
import { useCenteredTree } from "./helpers";
import { useState, useEffect } from "react";

import Popup from 'reactjs-popup';
import 'reactjs-popup/dist/index.css';

const containerStyles = {
  width: "100vw",
  height: "100vh"
};

// Here we're using `renderCustomNodeElement` to represent each node
// as an SVG `rect` instead of the default `circle`.
const renderRectSvgNode = ({ nodeDatum, toggleNode }) => (
  <Popup trigger={
    <g>
      <circle r="10" onClick={() => { console.log("Node datum: ", nodeDatum) }} />
      {nodeDatum.attributes != undefined &&
      <text fill="black" strokeWidth="1" x="20">
        {nodeDatum.name}
      </text>
  }
      {nodeDatum.attributes?.id && 
      (
        <text fill="black" x="20" dy="20" strokeWidth="1">
          ID: {nodeDatum.attributes?.id}
        </text>
      )}
      {nodeDatum.attributes == undefined && 
      (
        <text fill="black" x="20" dy="20" strokeWidth="1">
          Path sum: {nodeDatum.name}
        </text>
      )}
    </g>
  } modal nested>
    {
      close => (
        <div className='modal'>
          <div className='content'>
            <h3>Value: {nodeDatum.name}</h3>
            <h4>ID: {nodeDatum.attributes?.id}</h4>
          </div>
          <div>
            <button onClick=
              {() => close()}>
              Close modal
            </button>
          </div>
        </div>
      )
    }
  </Popup>
);

function transalteData(treeResponse) {
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
    visualTreeElems.set(key.toString(), { name: value.toString(), attributes: { "id": key.toString(), }, children: [] })
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
    visualTreeElems.get(lastNodeId.toString()).children.push({ name: sum.toString(), children: [] })
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



export default function App() {
  const [translate, containerRef] = useCenteredTree();

  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const getData = async () => {
      fetch(`http://localhost:8080/tree/getTree`)
        .then((response) => {
          if (response.ok) {
            return response.json(); // Parse the response data as JSON 
          } else {
            throw new Error('API request failed');
          }
        })
        .then((actualData) => {
          actualData = transalteData(actualData)
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
      console.log("Data: ", data)
    }
    getData()
  }, []);

  console.log("Data2: ", data)
  return (
    <div style={containerStyles} ref={containerRef}>
      {loading && <div>Data fetching...</div>}
      {error && (
        <div>{`There is a problem fetching the post data - ${error}`}</div>
      )}
      {data && <Tree
        data={data}
        translate={translate}
        renderCustomNodeElement={renderRectSvgNode}
        orientation="vertical"
      />}
    </div>
  );
}

// code based on: https://codesandbox.io/s/react-tree-visualizer-hs3l2