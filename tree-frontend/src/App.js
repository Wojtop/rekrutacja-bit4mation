import React from "react";
import Tree from "react-d3-tree";
import "./App.css";
import {useCenteredTree} from "./helpers";
import {useState, useEffect} from "react";

import Popup from 'reactjs-popup';
import 'reactjs-popup/dist/index.css';
import ActionsPopup from "./ActionsPopup";
import {translateData, getData} from "./helpers";

const containerStyles = {
    width: "100vw",
    height: "100vh"
};

// Here we're using `renderCustomNodeElement` to represent each node
// as an SVG `rect` instead of the default `circle`.
const renderRectSvgNode = ({nodeDatum, setData, setError, setLoading}) => (
    <Popup trigger={
        <g>
            <circle r="10" onClick={() => {
                console.log("Node datum: ", nodeDatum)
            }}/>
            {nodeDatum.attributes !== undefined &&
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
            {nodeDatum.attributes === undefined &&
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
                        <ActionsPopup nodeDatum={nodeDatum} setData={setData} setError={setError} setLoading={setLoading}/>
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
export default function App() {
    const [translate, containerRef] = useCenteredTree();

    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        getData(setData, setError, setLoading)
    }, []);

    // console.log("Data2: ", data)
    return (
        <div style={containerStyles} ref={containerRef}>
            {loading && <div>Data fetching...</div>}
            {error && (
                <div>{`There is a problem fetching the post data - ${error}`}</div>
            )}
            {data && <Tree
                data={data}
                translate={translate}
                // renderCustomNodeElement={renderRectSvgNode}
                renderCustomNodeElement={(rd3tProps) =>
                    renderRectSvgNode({ ...rd3tProps, setData, setError, setLoading })
                }
                orientation="vertical"
            />}
        </div>
    );
}

// code based on: https://codesandbox.io/s/react-tree-visualizer-hs3l2