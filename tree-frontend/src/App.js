import React from "react";
import Tree from "react-d3-tree";
import "./App.css";
import { useCenteredTree } from "./helpers";

const containerStyles = {
  width: "100vw",
  height: "100vh"
};

// Here we're using `renderCustomNodeElement` to represent each node
// as an SVG `rect` instead of the default `circle`.
const renderRectSvgNode = ({ nodeDatum, toggleNode }) => (
  <g>
    <circle r="10" onClick={toggleNode} />
    <text fill="black" strokeWidth="1" x="20">
      {nodeDatum.name}
    </text>
    {nodeDatum.attributes?.department && (
      <text fill="black" x="20" dy="20" strokeWidth="1">
        Department: {nodeDatum.attributes?.department}
      </text>
    )}
  </g>
);

const orgChartJson = {
  name: 'CEO',
  children: [
    {
      name: 'Manager',
      attributes: {
        department: 'Production',
      },
      children: [
        {
          name: 'Foreman',
          attributes: {
            department: 'Fabrication',
          },
          children: [
            {
              name: 'Worker',
            },
          ],
        },
        {
          name: 'Foreman',
          attributes: {
            department: 'Assembly',
          },
          children: [
            {
              name: 'Worker',
            },
          ],
        },
      ],
    },
  ],
};

export default function App() {
  const [translate, containerRef] = useCenteredTree();

  fetch('http://localhost:8080/tree/getTree') 
  .then(response => { 
    if (response.ok) { 
      return response.json(); // Parse the response data as JSON 
    } else { 
      throw new Error('API request failed'); 
    } 
  }) 
  .then(data => { 
    // Process the response data here 
    console.log(data); // Example: Logging the data to the console 
  }) 
  .catch(error => { 
    // Handle any errors here 
    console.error(error); // Example: Logging the error to the console 
  });

  return (
    <div style={containerStyles} ref={containerRef}>
      <Tree
        data={orgChartJson}
        translate={translate}
        renderCustomNodeElement={renderRectSvgNode}
        orientation="vertical"
      />
    </div>
  );
}
